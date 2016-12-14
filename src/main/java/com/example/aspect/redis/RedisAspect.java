package com.example.aspect.redis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.example.aspect.annotation.RedisCacheable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.*;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;


/**
 * Created by cheng on 2016/5/31 0031.
 */
@Aspect
@Component
public class RedisAspect {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShardedJedisPool jedisPool;


    //用于redis缓存切面
    @Pointcut("@annotation(com.example.aspect.annotation.RedisCacheable)")
    public void redisAspect() {

    }

    private ShardedJedis getJedis() {
        ShardedJedis jedis = jedisPool.getResource();
        //jedis.auth(auth);
        return jedis;
    }

    @Around(value = "redisAspect()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        //从redis中获取数据
        ShardedJedis jedis = getJedis();
        try {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            RedisCacheable cacheable = signature.getMethod().getAnnotation(RedisCacheable.class);
            Class aClass = signature.getReturnType();
            //获取redis 类型
            RedisType redisType = getRedisType(aClass);
            //如果是泛型获取泛型类型
            String[] genericParam = getGenericParam(signature);
            //创建redis key
            String key = getKey(pjp, cacheable);

            RedisCacheHelper redisCacheHelper = new RedisCacheHelper(jedis, key, redisType, genericParam);
            Object result = redisCacheHelper.get();
            if (result != null && !cacheable.forceUpdate()) {
                return result;
            }
            result = pjp.proceed();
            redisCacheHelper.set(result, cacheable.time());
            return result;
        } finally {
            jedis.close();
        }
    }

    private RedisType getRedisType(Class aClass) {
        RedisType redisType;
        if (List.class.isAssignableFrom(aClass)) {
            redisType = RedisType.JAVA_LIST;
        } else if (Map.class.isAssignableFrom(aClass)) {
            redisType = RedisType.JAVA_MAP;
        } else if (aClass.getName().equals("java.lang.String")) {
            redisType = RedisType.JAVA_STRING;
        } else if (Set.class.isAssignableFrom(aClass)) {
            redisType = RedisType.JAVA_SET;
        } else {
            redisType = RedisType.CUSTOM_TYPE;
        }
        return redisType;
    }

    private String getKey(ProceedingJoinPoint pjp, RedisCacheable cacheable) {
        String key = cacheable.key();
        if (cacheable.keysufixParamIndex().length > 0) {
            Object[] args = pjp.getArgs();
            for (int i : cacheable.keysufixParamIndex()) {
                key += ":" + args[i - 1];
            }
        }
        return key;
    }

    private String[] getGenericParam(MethodSignature signature) {
        String[] types = null;
        Type genericReturnType = signature.getMethod().getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            types = genericReturnType.toString().substring(genericReturnType.toString().indexOf("<") + 1, genericReturnType.toString().lastIndexOf(">")).split(",");
        }
        return types;
    }


    public static void main(String[] args) throws Throwable {
        JedisPool jedisPool = new JedisPool("192.168.153.130", 6379);
        Class aClass = Class.forName("com.alibaba.sports.wora.model.Address");
        RuntimeSchema<Object> schema = RuntimeSchema.createFrom(aClass);
        Jedis jedis = jedisPool.getResource();
        long size = jedis.llen("woraAddress:0");
        ArrayList<Object> result = new ArrayList();
        if (size > 0) {
            Object aClass1 = aClass.newInstance();
            for (int a = 0; a < size; a++) {
                byte[] bytes = jedis.lpop("woraAddress:0".getBytes());
                ProtostuffIOUtil.mergeFrom(bytes, aClass1, schema);
                result.add(aClass1);
            }
        } else {
        }
        System.out.println(result);
    }

}
