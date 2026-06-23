package com.cherry.base.exception;

import com.cherry.base.domain.constant.StringConstant;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.base.utils.CherryCollectionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.util.Objects;

/**
 * @author : ganxiongwen
 * Date: 2022/4/20 16:30
 * Description: global exception
 * ClassName: GlobalException
 * Package: com.cherry.common.exception
 * Copyright (c) 2022,All Rights Reserved.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handler global exception
     *
     * @param e global exception
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = CherryException.class)
    public CherryResponseEntity<String> handleCherryException(CherryException e) {
        log.error(
                "error code: {}, error message: {}, args: {}",
                e.getCode(),
                e.getMessage(),
                CherryCollectionUtil.arrayToString(e.getArgs())
        );
        return CherryResponseEntity.fail(e.getCode(), e.getMessage());
    }

    /**
     * handler MethodArgumentNotValidException exception
     *
     * @param e {@link MethodArgumentNotValidException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CherryResponseEntity<String> handleValidException(MethodArgumentNotValidException e) {
        return getFormatException(e);
    }

    /**
     * handler ConstraintViolationException
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public CherryResponseEntity<String> handleValidException(ConstraintViolationException e) {
        String message = "";
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            message = constraintViolation.getMessage();
            break;
        }
        log.error("constraint violation exception, error message: {}", message);
        return CherryResponseEntity.fail(message);
    }

    /**
     * handler BindException exception
     *
     * @param e {@link BindException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = BindException.class)
    public CherryResponseEntity<String> handleValidException(BindException e) {
        return getFormatException(e);
    }


    /**
     * handler Exception
     *
     * @param e {@link Exception}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = Exception.class)
    public CherryResponseEntity<String> handleException(Exception e) {
        log.error(
                "global exception handler, exception msg: {}, exception stack trace info: {}",
                e.getMessage(),
                getStackTrace(e)
        );
        return CherryResponseEntity.fail("处理失败，请稍后重试");
    }

    /**
     * handler MaxUploadSizeExceededException
     *
     * @param e {@link MaxUploadSizeExceededException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public CherryResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(
                "max upload size exception handler, exception msg: {}, exception stack trace info: {}",
                e.getMessage(),
                getStackTrace(e)
        );
        return CherryResponseEntity.fail("文件上传过大");
    }

    /**
     * handler RequestMappingInfoHandlerMapping
     *
     * @param e {@link HttpRequestMethodNotSupportedException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public CherryResponseEntity<String> handleRequestMappingInfoHandlerMapping(HttpRequestMethodNotSupportedException e) {
        URI uri = e.getBody().getInstance();
        String path = StringConstant.EMPTY;
        if (Objects.nonNull(uri)) {
            path = uri.getHost() + StringConstant.COLON + uri.getPort() + uri.getPath();
        }
        log.error(
                "url: {}, Request method {} is not supported, supported methods are: {}",
                path,
                e.getMethod(),
                e.getSupportedMethods()
        );
        return CherryResponseEntity.fail("接口请求错误");
    }

    /**
     * handler RequestMappingInfoHandlerMapping
     *
     * @param e {@link HttpRequestMethodNotSupportedException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = IllegalStateException.class)
    public CherryResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        log.error(
                "Illegal State Exception, exception msg: {}, exception stack trace info: {}",
                e.getMessage(),
                getStackTrace(e)
        );
        return CherryResponseEntity.fail("处理失败，请稍后重试");
    }

    /**
     * handler format exceptions
     *
     * @param e {@link BindException}
     * @return {@link CherryResponseEntity}
     */
    public CherryResponseEntity<String> getFormatException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        log.error(message);
        return CherryResponseEntity.fail(message);
    }

    /**
     * get the error message
     *
     * @param e {@link Exception}
     * @return error message string
     */
    @SuppressWarnings("unused")
    private static String getStackTrace(Exception e) {
        if (e == null) {
            return StringConstant.EMPTY;
        }
        return CherryCollectionUtil.arrayToString(e.getStackTrace());
    }
}