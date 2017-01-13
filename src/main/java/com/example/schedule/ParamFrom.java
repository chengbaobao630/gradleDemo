package com.example.schedule;

/**
 * Created by thinkpad on 2016/6/27.
 */
public enum ParamFrom {
    PRE_METHOD,
    HTTP_GET,
    PROPERTIES,
    XML,
    STREAM,

    HTTP_POST {
        public void getValue(MultiJobTask.MultiJobMethod jobMethod) {
            MultiJobTask jobTask=new MultiJobTask(10);
            jobTask.addMethod(jobMethod);
            jobTask.invoke();

        }
    }


}
