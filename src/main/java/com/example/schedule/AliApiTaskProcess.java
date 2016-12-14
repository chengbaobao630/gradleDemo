package com.example.schedule;


import com.example.schedule.task.ScheduleTaskProcess;
import org.springframework.stereotype.Component;

/**
 * Created by thinkpad on 2016/6/27.
 */
@Component
public class AliApiTaskProcess extends ScheduleTaskProcess {


    @Override
    protected void processWithCustom() throws Exception {
        if (task instanceof AliApiTask){
            AliApiTask apiTask= (AliApiTask) task;
            apiTask.invoke();
        }
    }

}
