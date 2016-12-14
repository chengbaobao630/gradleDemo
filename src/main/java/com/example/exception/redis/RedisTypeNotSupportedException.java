package com.example.exception.redis;

/**
 * Created by cheng on 2016/6/3 0003.
 */
public class RedisTypeNotSupportedException extends Exception {

    public RedisTypeNotSupportedException(String message) {
        super(message);
    }

    public RedisTypeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
