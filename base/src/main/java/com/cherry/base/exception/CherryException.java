package com.cherry.base.exception;


import com.cherry.base.utils.CherryStringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * @author : cherry
 * Date: 2022/4/20 16:30
 * Description: exception information
 * ClassName: CherryException
 * Package: com.cherry.flow.common.exception
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@Getter
@SuppressWarnings(value = "unused")
public class CherryException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * error codes
     */
    private final int code;

    /**
     * parameters corresponding to error codes
     */
    private final Object[] args;

    /**
     * error messages
     */
    private final String errorMessage;

    public CherryException(int code, Object[] args, String errorMessage) {
        this.code = code;
        this.args = args;
        this.errorMessage = errorMessage;
    }

    public CherryException(int code, Object[] args) {
        this(code, args, null);
    }

    public CherryException(int code, String errorMessage) {
        this(code, null, errorMessage);
    }

    public CherryException(BaseExceptionEnum baseExceptionEnum) {
        this(baseExceptionEnum.getErrorCode(), null, baseExceptionEnum.getErrorMsg());
    }

    public CherryException(BaseExceptionEnum baseExceptionEnum, Object[] args) {
        this(baseExceptionEnum.getErrorCode(), args, baseExceptionEnum.getErrorMsg());
    }

    public static void throwException(String msg, Object dataFlag) {
        log.error("{}, data flag: {}", msg, dataFlag);
        throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), msg);
    }

    public static void throwException(String msg) {
        log.error(msg);
        throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), msg);
    }

    /**
     * get the error message
     *
     * @return error message, which returns null if no error message is set
     */
    @Override
    public String getMessage() {
        String message = null;
        if (CherryStringUtil.isNotEmpty(errorMessage)) {
            message = errorMessage;
        }
        return message;
    }

}
