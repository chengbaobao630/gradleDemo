package com.example.controller;

import javassist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cheng on 2017/1/9 0009.
 */
@Service
public class HelloCc {

    @Autowired
    HelloCom helloCom;

    public void sayHello(String name) {
        System.out.println(name);
    }


}
