package com.example.rest;

import com.example.service.RedisService;
import com.example.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Component
@Path("/hello")
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    RedisService redisService;

    ObjectMapper mapper = new ObjectMapper();

    @GET
    public String message() {
        try {
            return mapper.writeValueAsString(userService.getUsers());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "ok";
        }
    }

    @GET
    @Path("/redis/del")
    public String delRedis(){
        boolean b=redisService.clearRedis();
        return b ? "success" : "failed";
    }

}