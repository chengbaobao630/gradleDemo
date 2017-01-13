package com.example.controller;

import org.springframework.stereotype.Component;

/**
 * Created by cheng on 2017/1/10 0010.
 */
@Component
public class HelloCom {


    public String getHelloString(String name) {
        return "hello :" + name;
    }
}
