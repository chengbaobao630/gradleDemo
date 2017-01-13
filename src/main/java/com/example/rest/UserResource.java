package com.example.rest;

import cc.home.bo.UserBo;
import com.example.service.RedisService;
import com.example.service.dubbo.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;

@Component
@Path("/hello")
public class UserResource {

    @Inject
    UserServiceImpl userServiceImpl;

    @Inject
    RedisService redisService;

    ObjectMapper mapper = new ObjectMapper();

    @GET
    public String message() {
        try {
            return mapper.writeValueAsString(userServiceImpl.getUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return "ok";
        }
    }

    @POST
    @Path("user")
    public String setUser(){
        UserBo userBo=new UserBo();
        userBo.setUsername("cc");
        userBo.setPassword("123465");
        UserBo userBo1=new UserBo();
        userBo1.setUsername("dd");
        userBo1.setPassword("654321");

        userServiceImpl.addUser(new ArrayList<UserBo>(){{
            add(userBo);
            add(userBo1);
        }});
        return "success";
    }

    @GET
    @Path("/redis/del")
    public String delRedis(){
        boolean b=redisService.clearRedis();
        return b ? "success" : "failed";
    }

}