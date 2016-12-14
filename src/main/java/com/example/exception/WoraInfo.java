package com.example.exception;

/**
 * Created by cheng on 2016/6/1 0001.
 */
public enum WoraInfo {

    //默认状态
    SUCCESS(1,"SUCCESS"),
    //参数问题
    ARG_LOSS(3000, "缺少必要的参数"),
    MD5_WRONG(3001, "md5检验码错误"),
    PARAM_ERR(3002, "参数错误"),
        
    MSG_SED_OK(60001,"验证码发送成功"),
    MSG_SED_FAILED(60002,"号码不存在/发送失败"),
    MSG_SED_REPEAT(60003,"不能重复发送"),
    MSG_SED_VALIDATE(60004,"参数不能为空!/安全验证不通过!"),
    MSG_SED_VALIDATE_PASS(60005,"验证码正确"),
    MSG_SED_VALIDATE_ERR(60006,"验证码错误"),
    MSG_SED_VALIDATE_TIMEOUT(60007,"验证码过期"),
    MSG_SED_VALIDATE_EXP(60008,"验证码验证时异常"),
    MSG_SED_EXP(60009,"验证码验证时异常"),
    
    SAFE_VALIDATE_PASS(60010,"安全验证通过"),
    SAFE_VALIDATE_FAILED(60011,"安全验证不通过"),

    REDIS_TYPE_NOT_SUPPORT(7000,"redis不支持该数据类型"),

    DB_SELECT_FAILED(8000,"获取数据失败"),
    DB_INSERT_FAILED(8001,"保存数据失败"),
    DB_UPDATE_FAILED(8002,"更新数据失败"),
    DB_DELETE_FAILED(8003,"删除数据失败"),
    ALI_CONNECT_FAILED(9000,"阿里连接失败"),

    //总报错
    NOT_FOUND(9001, "查找的数据不存在"),
    //用户相关错误码
    USERS_NO_LOGIN(10000,"用户尚未登陆"),
    USERS_NOT_FOUND(10001, "当前用户不存在"),
    USERS_BINDING(10002, "用户已绑定"),
    USERS_NOT_BINDING(10003, "用户尚未绑定"),
    USERS_NOT_BINDING_BUT_SIGNUP(10004, "用户已参赛但尚未绑定"),
    USERS_BINDING_ERROR(10005, "用户绑定异常"),
    //赛事相关
    EVENTS_NOT_FOUND(11001,"未查询到赛事或已关闭"),
    EVENTS_CLOSED(11011,"赛事已结束"),
    EVENTS_NOT_START(11012,"赛事尚未开始"),
    EVENTS_IS_FULL(11013,"赛事报名人数已满"),
    //赛事报名相关
    EVENTS_SIGN_NOT_FOUND(12001,"未查询到报名信息"),
    EVENTS_SIGN_CLOSED(12011,"该报名已结束"),
    EVENTS_SIGN_NOT_START(12012,"该报名尚未开始"),
    EVENTS_SIGN_NOT_PAY(12012,"该报名尚尚未支付"),
    EVENTS_SIGN_EXIST(13001,"已报名此比赛"),
    EVENTS_SIGN_NOT_EXIST(13002,"未报名此比赛"),
    EVENTS_SIGN_EVENTS_NOT_EXIST(13003,"未报名任何比赛"),
    EVENTS_SIGN_EVENTS_NOT_START(13004,"报名的比赛尚未开始"),
    EVENTS_SIGN_EXIST_CONFLICT(13005,"赛事报名时间冲突"),
    
    //app相关
    APP_NOT_FOUND(14001, "app不存在，请检查appKey");
    

    private int code;

    private String message;

    WoraInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WoraInfo valueOf(int code){
        for ( WoraInfo exceptioncode:values()){
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
