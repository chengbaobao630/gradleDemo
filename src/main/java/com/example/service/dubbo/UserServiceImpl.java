package com.example.service.dubbo;

import cc.home.bo.UserBo;
//import com.alibaba.dubbo.config.annotation.Service;
import com.example.aspect.annotation.RedisCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import reomte.dubbo.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cheng on 2016/11/10 0010.
 */
@Service("userService")
public class UserServiceImpl implements UserService{

    @Autowired
    private MongoTemplate mongoTemplate;

    private ObjectMapper mapper=new ObjectMapper();

    @RedisCache(key = "users")
    public String getUsers() {
        List<UserBo> users = mongoTemplate.findAll(UserBo.class);
        HashMap<String, List<UserBo>> map = new HashMap<>();
        map.put("users", users);
        try {
            return map != null ? mapper.writeValueAsString(map) : "no results";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public void addUser(ArrayList<UserBo> userBos) {
            mongoTemplate.insert(userBos,UserBo.class);
    }
}
