package com.example.schedule.task;

/**
 * Created by cheng on 2016/6/13 0013.
 */

public abstract class ScheduleTaskProcess implements TaskProcess {

    protected Task task;


    public void reset(ScheduleTask task){
        this.task=task;
    }

    public ScheduleTaskProcess() {
    }

    @Override
    public void process(Task task) throws Exception {
            reset((ScheduleTask) task);
            processWithCustom();

    }

    protected abstract void processWithCustom() throws Exception;


}
