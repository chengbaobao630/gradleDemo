package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by cheng on 2016/11/10 0010.
 */
@Service
public class RedisService {

    @Autowired
    ShardedJedisPool jedisPool;

    @Autowired
    RedisTemplate redisTemplate;


    public boolean clearRedis(){
        ShardedJedis resource = null;
        try {
            resource = jedisPool.getResource();
            resource.del("user".getBytes());
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if (resource != null){
                resource.close();
            }
        }
    }

}
