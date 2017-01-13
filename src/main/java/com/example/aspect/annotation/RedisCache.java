package com.example.aspect.annotation;



import java.lang.annotation.*;

/**
 * Created by cheng on 2016/6/3 0003.
 * 用于通过切面自动开启redis缓存
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCache {

    /**
     * 缓存在redis中的键
     * @return
     */
    String key();

    /**
     * 缓存在redis中的时间，默认一天
     * @return
     */
    int time() default 60*60*24;

    /**
     * 此方法结束是否需要更新redis
     * 可用于update类似方法上
     * @return
     */
    boolean forceUpdate() default false;

    /**
     * 把某个参数作为reids key的后缀
     * 使用索引来查找
     * @return
     */
    int[] keysufixParamIndex() default {};


}
