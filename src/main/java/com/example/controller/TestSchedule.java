package com.example.controller;

import com.example.schedule.MultiJobTask;
import com.example.schedule.MultiJobTaskProcess;
import com.example.schedule.ParamFrom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by cheng on 2017/1/9 0009.
 */
@RestController
@RequestMapping("test")
public class TestSchedule {

    @Autowired
    HelloCc helloCc;

    @Autowired
    HelloCom helloCom;

    @RequestMapping("schedule")
    public String schedule(String name) {
        try {
            MultiJobTask multiJobTask = new MultiJobTask(30);
            multiJobTask.setProcessHandler(new MultiJobTaskProcess());
            ArrayList<MultiJobTask.MultiJobMethod> arrayList = new ArrayList();
            MultiJobTask.MultiJobMethod multiJobMethod1 =
                    multiJobTask.new MultiJobMethod(helloCom, "getHelloString", new Object[]{name});

            MultiJobTask.MultiJobMethod multiJobMethod =
                    multiJobTask.new MultiJobMethod(helloCc, "sayHello", new Object[]{ParamFrom.PRE_METHOD});
            arrayList.add(multiJobMethod1);
            arrayList.add(multiJobMethod);
            multiJobTask.setMethods(arrayList);
            multiJobTask.register();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
