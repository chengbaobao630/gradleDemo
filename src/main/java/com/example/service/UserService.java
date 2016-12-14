package com.example.service;

import com.example.aspect.annotation.RedisCacheable;
import com.example.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cheng on 2016/11/10 0010.
 */
@Service
public class UserService {

    @Autowired
    MongoTemplate mongoTemplate;


    @RedisCacheable(key = "users")
    public HashMap<String,List<User>> getUsers(){
        List<User> users=mongoTemplate.findAll(User.class);
        HashMap<String,List<User>> map=new HashMap<>();
        map.put("users",users);
        return  map != null ? map : null;
    }

}
