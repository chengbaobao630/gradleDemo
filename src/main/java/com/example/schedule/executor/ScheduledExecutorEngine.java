package com.example.schedule.executor;


import com.example.schedule.task.Task;
import com.example.schedule.task.TaskProcess;
import com.example.schedule.task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by cheng on 2016/6/12 0012.
 */
@Component
public class ScheduledExecutorEngine {

    private static Logger log= LoggerFactory.getLogger(ScheduledExecutorEngine.class);

    private static List<Task> taskList=new CopyOnWriteArrayList();

    private static List<Task> errorList=new LinkedList<>();

    protected static void addTaskList(Task task) {
        taskList.add(task);
    }


    @Resource(name = "scheduledExec")
    private   ScheduledExecutorService executorService;

    private Semaphore currency;

    @PostConstruct
    private  void  init(){
        currency=new Semaphore(Math.max(1,Runtime.getRuntime().availableProcessors()));
        executorService.scheduleWithFixedDelay(new Executor(), 60,300, TimeUnit.SECONDS);
    }

    class Executor implements Runnable{

        @Override
        public void run() {
            Iterator it=taskList.iterator();
            while (it.hasNext()){
                Task task;
                try {
                     task=(Task) it.next();
                    taskList.remove(task);
                    if (task.getTimes()>task.MAX_RETRY_TIMES){
                        task.setStatus(TaskStatus.ABANDON);
                        errorList.add(task);
                        continue;
                    }else {
                        if (task.getStatus().compareTo(TaskStatus.PROCESSING)==0){
                            continue;
                        }else if (task.getStatus().compareTo(TaskStatus.REDO)==0){
                            task.setStatus(TaskStatus.PROCESSING);
                            log.info("task:"+task+"redo");
                        }else {
                            task.setStatus(TaskStatus.PROCESSING);
                        }
                    }
                     executorService.schedule(new EngineTask(task) , task.getDelayTime(), TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }finally {
                }

            }
        }
    }

    class EngineTask implements Runnable{
        private Task task;

        public EngineTask(Task task) {
            this.task = task;
        }

        @Override
            public void run() {
            try {
                currency.tryAcquire(500, TimeUnit.MILLISECONDS);
                log.info("task begin to start:"+task);
                TaskProcess process= task.getProcessHandler();
                process.process(task);
                task.setStatus(TaskStatus.DOWN);
            }catch (Exception e){
                task.setStatus(TaskStatus.REDO);
                taskList.add(task);
                log.error("ScheduledExecutorEngine due to error",e);
            }finally {
                task.increaseTimes();
                currency.release();
            }
        }
    }

}
