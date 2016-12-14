package com.example.exception;

/**
 * Created by cheng on 2016/6/1 0001.
 */
public enum ErrorInfo {

    ALI_CONNECT_FAILED(9000,"阿里连接失败"),
    DB_SELECT_FAILED(8000,"获取数据失败"),
    DB_INSERT_FAILED(8001,"保存数据失败"),
    DB_UPDATE_FAILED(8002,"更新数据失败"),
    DB_DELETE_FAILED(8003,"删除数据失败"),
    NO_LOGIN(10000,"用户尚未登陆"),
    REDIS_TYPE_NOT_SUPPORT(7000,"redis不支持该数据类型");

    private int code;

    private String message;

    ErrorInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorInfo valueOf(int code){
        for ( ErrorInfo exceptioncode:values()){
            if (exceptioncode.code==code){
                return exceptioncode;
            }
        }
        return null;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return message;
    }

    public void setDescription(String description) {
        this.message = description;
    }
}
