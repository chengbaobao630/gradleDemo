package com.example.schedule.executor;


import com.example.schedule.task.Task;

/**
 * Created by cheng on 2016/6/13 0013.
 */
public class ScheduledExecutorListener {

    public  static void register(Task task){
        ScheduledExecutorEngine.addTaskList(task);
    }

    public  static void remove(Task task){
    }
}
