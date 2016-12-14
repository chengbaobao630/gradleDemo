package com.example.schedule;


import com.example.schedule.task.ScheduleTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thinkpad on 2016/6/27.
 */
public class AliApiTask extends ScheduleTask {

    private List<AliTaskMethod> methods = new ArrayList<>();

    public List<AliTaskMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<AliTaskMethod> methods) {
        this.methods = methods;
    }

    public void addMethod(AliTaskMethod method) {
        this.methods.add(method);
    }

    public AliApiTask(long delayTime) {
        super(delayTime);
        this.setProcessHandler();
    }

    private AliTaskMethod preMethod = null;

    @Override
    public void setProcessHandler() {
//        processHandler = (TaskProcess) SpringContextUtil.getBean("aliApiTaskProcess");
    }

    public void invoke() {
        Object preParam = null;
        for (AliTaskMethod method : methods) {
            try {
                for (int i = 0; i < method.getParams().length; i++) {
                    if (method.getParams()[i] instanceof ParamFrom) {
                        ParamFrom paramFrom = (ParamFrom) method.getParams()[i];
                        if (paramFrom.compareTo(ParamFrom.PRE_METHOD) == 0) {
                            method.getParams()[i] = preParam;
                        }
                    }
                }
                Object result = method.getMethod().invoke(method.getInvokeObject(), method.getParams());
                method.setReturnType(result.getClass());
                if (result != null) {
                    preParam = result;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    class AliTaskMethod {

        private Object invokeObject;

        private String methodName;

        private Object[] params;

        private Method method;

        private ParamFrom from;

        private Class returnType;

        public AliTaskMethod(Object invokeObject, String methodName, Object[] params) throws NoSuchFieldException, IllegalAccessException {
            this.invokeObject = invokeObject;
            this.methodName = methodName;
            this.params = params;
            Class[] classes = new Class[params.length];
            for (int a = 0; a < params.length; a++) {
                Class clazz = params[a].getClass();
                if (params[a].getClass().isPrimitive()) {
                    Field type = clazz.getDeclaredField("TYPE");
                    clazz = (Class) type.get(null);
                }
                if (params[a] instanceof ParamFrom) {
                    if (ParamFrom.PRE_METHOD.compareTo((ParamFrom) params[a]) == 0) {
                     clazz = preMethod.getReturnType();
                    }
                }
                classes[a] = clazz;
            }
            try {
                this.method = invokeObject.getClass().getDeclaredMethod(methodName, classes);
                this.returnType=method.getReturnType();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                preMethod = this;
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
