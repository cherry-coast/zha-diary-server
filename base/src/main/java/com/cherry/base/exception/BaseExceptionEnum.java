package com.cherry.base.exception;

import lombok.Getter;

/**
 * @author cherry
 * @version 1.0.0
 */
@Getter
@SuppressWarnings("unused")
public enum BaseExceptionEnum {

    /**
     * 成功
     */
    SUCCESS(200, "success"),
    /**
     * 失败
     */
    FAIL(500, "fail"),

    /**
     * 失败
     */
    NO_AUTHORIZE(401, "no authorize"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(500501, "system error"),
    /**
     * 数据库异常
     */
    DATABASE_ERROR(500504, "database error"),
    /**
     * 未知异常
     */
    UNKNOWN_ERROR(500505, "unknown error"),
    /**
     * json格式化异常
     */
    JSON_ERROR(500506, "json parse error"),
    /**
     * thread异常
     */
    THREAD_ERROR(500507, "thread error"),
    /**
     * reflect 异常
     */
    REFLECT_ERROR(500508, "reflect error"),
    /**
     * 接口 异常
     */
    INTERFACE_ERROR(500508, "interface error"),
    ;

    /**
     * user type code
     */
    private final Integer errorCode;

    /**
     * user type name
     */
    private final String errorMsg;

    BaseExceptionEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public static String getMsg(Integer errorCode) {
        for (BaseExceptionEnum baseExceptionEnum : BaseExceptionEnum.values()) {
            if (baseExceptionEnum.getErrorCode().equals(errorCode)) {
                return baseExceptionEnum.getErrorMsg();
            }
        }
        return "";
    }
}
