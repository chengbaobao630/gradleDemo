package com.example.schedule.task;


import com.example.schedule.executor.ScheduledExecutorListener;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cheng on 2016/6/13 0013.
 */
public abstract class ScheduleTask implements Task {

    private long delayTime;

    private Integer times = 0;

    private TaskStatus taskStatus;

    private String taskNum = "";

    private static Integer totalTask = 0;

    protected TaskProcess processHandler;


    public ScheduleTask(long delayTime) {
        totalTask++;
        this.delayTime = delayTime;
        this.taskStatus = TaskStatus.NEW;
        synchronized (totalTask) {
            taskNum=this.getClass().getSimpleName() + ":" + (totalTask - 1 );
            System.out.println(taskNum);
        }
    }

    public void register() {
        ScheduledExecutorListener.register(this);
    }

    @Override
    public String getTaskNum() {
        return this.taskNum;
    }

    @Override
    public Long getDelayTime() {
        return this.delayTime;
    }

    public <T extends TaskProcess> void setProcessHandler(T t) {
        this.processHandler = t;
    }

    @Override
    public TaskProcess getProcessHandler() {
        return this.processHandler;
    }

    @Override
    public TaskStatus getStatus() {
        return this.taskStatus;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.taskStatus = status;
    }

    @Override
    public Integer getTimes() {
        return times;
    }

    @Override
    public void increaseTimes() {
        this.times++;
    }

}
