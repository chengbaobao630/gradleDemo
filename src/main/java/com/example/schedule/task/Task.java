package com.example.schedule.task;

/**
 * Created by cheng on 2016/6/13 0013.
 */
public interface Task {

    Integer MAX_RETRY_TIMES=5;

    TaskStatus getStatus();

    void setStatus(TaskStatus status);

    Integer getTimes();

    void increaseTimes();

    Long getDelayTime();

    TaskProcess getProcessHandler();

    String toString();


}
