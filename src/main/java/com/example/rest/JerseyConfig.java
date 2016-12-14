package com.example.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * Created by cheng on 2016/11/10 0010.
 */
public class JerseyConfig  extends ResourceConfig {
    public JerseyConfig() {
        register(RequestContextFilter.class);
        packages("com.example.rest");
        register(LoggingFilter.class);
    }
}
