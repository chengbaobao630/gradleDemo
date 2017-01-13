package com.example.service.dubbo;

import cc.home.bo.UserBo;
import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import reomte.dubbo.TestDubbo;

/**
 * Created by cheng on 2017/1/12 0012.
 */
@Service
public class TestDubboService implements TestDubbo {

    private ObjectMapper mapper=new ObjectMapper();


    @Autowired
    MongoTemplate template;

    @Override
    public String hello(String s) {
        try {
            return mapper.writeValueAsString(template.findAll(UserBo.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
