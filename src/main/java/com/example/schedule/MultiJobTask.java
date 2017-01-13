package com.example.schedule;


import com.example.schedule.task.ScheduleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thinkpad on 2016/6/27.
 */
public class MultiJobTask extends ScheduleTask {


    private static final Logger LOG = LoggerFactory.getLogger(MultiJobTask.class);

    private static ThreadLocal<MultiJobMethod> threadLocal
            = new ThreadLocal<>();

    private List<MultiJobMethod> methods = new ArrayList<>();

    public List<MultiJobMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<MultiJobMethod> methods) {
        this.methods = methods;
    }

    public void addMethod(MultiJobMethod method) {
        this.methods.add(method);
    }

    public MultiJobTask(long delayTime) {
        super(delayTime);
    }

    private MultiJobMethod preMethod = null;

    public Object invoke() {

        Object preParam = null;

        Object result = null;
        for (MultiJobMethod method : getMethods()) {
            try {
                for (int i = 0; i < method.getParams().length; i++) {
                    if (method.getParams()[i] instanceof ParamFrom) {
                        ParamFrom paramFrom = (ParamFrom) method.getParams()[i];
                        if (paramFrom.compareTo(ParamFrom.PRE_METHOD) == 0) {
                            method.getParams()[i] = preParam;
                        }
                    }
                }
                result = method.getMethod().invoke(method.getInvokeObject(), method.getParams());
                if (result == null) {
                    return null;
                }
                method.setReturnType(result.getClass());
                preParam = result;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    public class MultiJobMethod {

        private Object invokeObject;

        private String methodName;

        private Object[] params;

        private Method method;

        private ParamFrom from;

        private Class returnType;

        public MultiJobMethod(Object invokeObject, String methodName, Object[] params) throws NoSuchFieldException, IllegalAccessException {
            this.invokeObject = invokeObject;
            this.methodName = methodName;
            this.params = params;
            Class[] classes = new Class[params.length];
            for (int a = 0; a < params.length; a++) {
                if (params[a] == null) {
                    continue;
                }
                Class clazz = params[a].getClass();
                if (params[a].getClass().isPrimitive()) {
                    Field type = clazz.getDeclaredField("TYPE");
                    clazz = (Class) type.get(null);
                }
                if (params[a] instanceof ParamFrom) {
                    if (ParamFrom.PRE_METHOD.compareTo((ParamFrom) params[a]) == 0) {
                        preMethod = threadLocal.get();
                        if (preMethod == null) {
                            LOG.error("preMethod must not be null");
                        }
                        clazz = preMethod.getReturnType();
                    }
                }
                classes[a] = clazz;
            }
            try {
                this.method = invokeObject.getClass().getDeclaredMethod(methodName, classes);
                this.returnType = method.getReturnType();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                threadLocal.set(this);
            }
        }


        public Method getMethod() {
            return method;
        }

        public Object getInvokeObject() {
            return invokeObject;
        }

        public void setInvokeObject(Object invokeObject) {
            this.invokeObject = invokeObject;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Object[] getParams() {
            return params;
        }

        public void setParams(Object[] params) {
            this.params = params;
        }

        public ParamFrom getFrom() {
            return from;
        }

        public void setFrom(ParamFrom from) {
            this.from = from;
        }

        public Class getReturnType() {
            return returnType;
        }

        public void setReturnType(Class returnType) {
            this.returnType = returnType;
        }
    }


}
