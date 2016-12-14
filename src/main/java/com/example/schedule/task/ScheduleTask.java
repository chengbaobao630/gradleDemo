package com.example.schedule.task;


import com.example.schedule.executor.ScheduledExecutorListener;

/**
 * Created by cheng on 2016/6/13 0013.
 */
public abstract class ScheduleTask implements Task {

    private long delayTime;

    private Integer times=0;

    private TaskStatus taskStatus;

    protected TaskProcess processHandler;


    public ScheduleTask(long delayTime) {
        this.delayTime = delayTime;
        this.taskStatus=TaskStatus.NEW;
        this.setProcessHandler();
        ScheduledExecutorListener.register(this);
    }

    @Override
    public Long getDelayTime() {
        return this.delayTime;
    }

    public abstract void setProcessHandler();

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
        this.taskStatus=status;
    }

    @Override
    public Integer getTimes() {
        return times;
    }

    @Override
    public void increaseTimes(){
        this.times++;
    }

}
