package com.example.aspect.redis;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.example.exception.ExceptionInfo;
import com.example.exception.redis.RedisTypeNotSupportedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by cheng on 2016/6/8 0008.
 */
public class RedisHandler {

    private static final Logger LOG= LoggerFactory.getLogger(RedisHandler.class);

    private ShardedJedis jedis;

    private String key;

    private RedisType redisType;

    private String[] genericParam;

    private Integer expireTime;

    private ObjectMapper mapper=new ObjectMapper();


    private static final Map<String, Schema> schemaMap = new ConcurrentHashMap<>();

    public RedisHandler(ShardedJedis jedis, String key, RedisType redisType, String[] genericParam) throws RedisTypeNotSupportedException {
        this.jedis = jedis;
        this.key = key;
        this.redisType = redisType;
        if (genericParam!=null) {
            if (genericParam.length > 2) {
                throw new RedisTypeNotSupportedException("不支持三个泛型参数以上的类型");
            } else {
                this.genericParam=genericParam;
            }
        }
    }

    public Object get() throws IllegalAccessException, InstantiationException, ClassNotFoundException, RedisTypeNotSupportedException {
        Object result;
        switch (redisType.toString()) {
            case "JAVA_STRING":
                result = getString();
                break;
            case "JAVA_LIST":
                result = getList();
                break;
            case "JAVA_SET":
                result = getSet();
                break;
            case "JAVA_MAP":
                result = getMap();
                break;
            case "CUSTOM_TYPE":
                result = getCustomType();
                break;
            default:
                throw new RedisTypeNotSupportedException(ExceptionInfo.REDIS_TYPE_NOT_SUPPORT.getDescription());
        }
        return result;
    }

    private Schema getSchema(Class clazz) {
        Schema schema = schemaMap.get(clazz.toString());
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            schemaMap.put(clazz.toString(), schema);
        }
        return schema;
    }


    private Object getCustomType() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);

        byte[] bytes = jedis.get(key.getBytes());
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        Object o = clazz.newInstance();
        ProtostuffIOUtil.mergeFrom(bytes, o, schema);
        return o;
    }

    private Object getSet() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);
        Set resultSet = new HashSet();
        Set<byte[]> bytes = jedis.smembers(key.getBytes());
        if (bytes.size() <= 0) {
            return null;
        }
        for (byte[] b : bytes) {
            Object instance = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(b, instance, schema);
            resultSet.add(instance);
        }
        return resultSet;

    }

    private Object getMap() throws ClassNotFoundException, RedisTypeNotSupportedException, IllegalAccessException, InstantiationException {
        try {
            Object stringObj=getString();
            return stringObj == null ?
                    null : mapper.readValue((String) stringObj,Map.class);
        } catch (IOException e) {
            LOG.error("cast String to map due to error : " + getString());
            return null;

        }
        /*if (genericParam[0] == null || genericParam[1] == null) {
            throw new RedisTypeNotSupportedException("Map 类型需要两个泛型参数");
        }
        Class firstClass = Class.forName(genericParam[0]);
        Class secondClass = Class.forName(genericParam[1].trim());
        Schema firstSchema = getSchema(firstClass);
        Schema secondSchema = getSchema(secondClass);
        Map resultMap = new HashMap();
        Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
        if (map.size() <= 0) {
            return null;
        }
        Iterator<Map.Entry<byte[], byte[]>> it = map.entrySet().iterator();
        while (it.hasNext()){
            Object firstObject = firstClass.newInstance();
            Object secondObject = secondClass.newInstance();
            Map.Entry<byte[], byte[]> entry = it.next();
            ProtostuffIOUtil.mergeFrom(entry.getKey(), firstObject, firstSchema);
            ProtostuffIOUtil.mergeFrom(entry.getValue(), secondObject, secondSchema);
            resultMap.put(firstObject, secondObject);
        }
        return resultMap;*/

    }

    private Object getList() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);
        long size = jedis.llen(key);
        ArrayList<Object> result = new ArrayList();

        if (size > 0) {
            for (byte[] bytes : jedis.lrange(key.getBytes(), 0, size)) {
                Object instance = clazz.newInstance();
                ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
                result.add(instance);
            }
        } else {
            return null;
        }
        return result;
    }

    private Object getString() {
        String result = jedis.get(key);
        if (result==null|| StringUtils.isBlank(result)) {
            return null;
        }
        return result;
    }

    public void set(Object o) throws ClassNotFoundException, InstantiationException, RedisTypeNotSupportedException, IllegalAccessException {
        set(o,0);
    }

    public void set(Object o,Integer expireTime) throws IllegalAccessException, InstantiationException, ClassNotFoundException, RedisTypeNotSupportedException {
        if (expireTime!=null&&expireTime>0){
            this.expireTime=expireTime;
        }
        switch (redisType.toString()) {
            case "JAVA_STRING":
                handleString(o);
                break;
            case "JAVA_LIST":
                handleList(o);
                break;
            case "JAVA_SET":
                handleSet(o);
                break;
            case "JAVA_MAP":
                handleMap(o);
                break;
            case "CUSTOM_TYPE":
                handleCustomType(o);
                break;
            default:
                throw new RedisTypeNotSupportedException(ExceptionInfo.REDIS_TYPE_NOT_SUPPORT.getDescription());
        }
    }

    private void handleSet(Object os) throws ClassNotFoundException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);
        
        jedis.del(key);
        Set set = (Set) os;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            byte[] bytes = ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            jedis.sadd(key.getBytes(), bytes);
        }
        jedis.expire(key, expireTime);
    }

    private void handleString(Object os) {
        jedis.del(key);
        jedis.setex(key, expireTime, os.toString());
    }

    private void handleList(Object os) throws ClassNotFoundException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);
        
        jedis.del(key);
        List list = (List) os;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            byte[] bytes = ProtostuffIOUtil.toByteArray(o, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            jedis.lpush(key.getBytes(), bytes);
        }
        jedis.expire(key.getBytes(), expireTime);
    }

    private void handleMap(Object os) throws RedisTypeNotSupportedException, ClassNotFoundException {
        try {
            String value=mapper.writeValueAsString(os);
            handleString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
       /* if (genericParam[0] == null || genericParam[1] == null) {
            throw new RedisTypeNotSupportedException("Map 类型需要两个泛型参数");
        }
        Class firstClass = Class.forName(genericParam[0]);
        Class secondClass = Class.forName(genericParam[1].trim());
        Schema firstSchema = getSchema(firstClass);
        Schema secondSchema = getSchema(secondClass);
        jedis.del(key);
        Map map = (Map) os;
        Map<byte[], byte[]> byteMap = new LinkedHashMap<>();
        Iterator<Map.Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            byte[] firstByte = ProtostuffIOUtil.toByteArray(entry.getKey(), firstSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            byte[] secondByte = ProtostuffIOUtil.toByteArray(entry.getValue(), secondSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            byteMap.put(firstByte, secondByte);
        }
        jedis.hmset(key.getBytes(), byteMap);
        jedis.expire(key.getBytes(), expireTime);*/
    }

    private void handleCustomType(Object os) throws ClassNotFoundException {
        Class clazz = Class.forName(genericParam[0]);
        Schema schema = getSchema(clazz);
        
        jedis.del(key);
        byte[] bytes = ProtostuffIOUtil.toByteArray(os, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        jedis.set(key.getBytes(), bytes);
        jedis.expire(key.getBytes(), expireTime);
    }

}
