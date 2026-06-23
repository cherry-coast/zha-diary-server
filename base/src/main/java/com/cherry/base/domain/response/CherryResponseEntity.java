package com.cherry.base.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : cherry
 * Date: 2023/3/21 11:30
 * Description: 服务返回基类
 * ClassName: CherryResponseEntity
 * Package: com.cherry.common.domain
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@ToString
@SuppressWarnings(value = "unused")
@Tag(name = "统一返回对象")
public class CherryResponseEntity<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;


    /**
     * 授权过期
     */
    public static final int LICENSE_EXPIRE = 500401;

    /**
     * 登录过期
     */
    public static final int NO_AUTHORIZATION = 401;

    /**
     * 失败
     */
    public static final int FAIL = 500;

    @Schema(description = "服务内部响应码")
    private int code;

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "返回的数据")
    private T data;

    @Schema(description = "请求是否成功")
    private boolean success;

    public static <T> CherryResponseEntity<T> success() {
        return restResult(null, SUCCESS, null, true);
    }

    public static <T> CherryResponseEntity<T> success(T data) {
        return restResult(data, SUCCESS, "", true);
    }

    public static <T> CherryResponseEntity<T> success(T data, String msg) {
        return restResult(data, SUCCESS, msg, true);
    }

    public static <T> CherryResponseEntity<T> fail() {
        return restResult(null, FAIL, null, false);
    }

    public static <T> CherryResponseEntity<T> fail(String msg) {
        return restResult(null, FAIL, msg, false);
    }

    public static <T> CherryResponseEntity<T> authFail(String msg) {
        return restResult(null, NO_AUTHORIZATION, msg, false);
    }

    public static <T> CherryResponseEntity<T> fail(T data) {
        return restResult(data, FAIL, null, false);
    }

    public static <T> CherryResponseEntity<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg, false);
    }

    public static <T> CherryResponseEntity<T> fail(int code, String msg) {
        return restResult(null, code, msg, false);
    }

    public static <T> CherryResponseEntity<T> licenseExpire() {
        return restResult(null, LICENSE_EXPIRE, "该用户授权已过期，请输入激活码解锁完成登录", false);
    }

    /**
     * 生成一个包含指定数据、状态码、消息和成功的 CherryResponseEntity 对象
     *
     * @param data    数据
     * @param code    状态码
     * @param msg     消息
     * @param success 是否成功
     * @param <T>     数据类型
     * @return 包含指定数据、状态码、消息和成功的 CherryResponseEntity 对象
     */
    protected static <T> CherryResponseEntity<T> restResult(T data, int code, String msg, boolean success) {
        CherryResponseEntity<T> apiResult = new CherryResponseEntity<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setSuccess(success);
        return apiResult;
    }


}