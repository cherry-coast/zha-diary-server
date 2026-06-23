package com.cherry.base.worker;

import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.wrapper.ObjectMapperWrapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年08月22日 11:19:00
 * ClassName CherryJsonWorker
 * packageName com.cherry.base.worker
 */
@SuppressWarnings("unused")
public class CherryJsonWorker {

    private static final Logger log = LoggerFactory.getLogger(CherryJsonWorker.class);


    private volatile static ObjectMapper objectMapper;

    private CherryJsonWorker() {}

    /*
     * 单例模式-饿汉式
     */
    static {
        objectMapper = ObjectMapperWrapper.getObjectMapper();
    }

    /**
     * 单例模式-懒汉式-dcl 防止以后使用，目前留着
     * @return {@link ObjectMapper}
     */
    public static ObjectMapper getInstance() {
        if (Objects.nonNull(objectMapper)) {
            return objectMapper;
        }
        synchronized (CherryJsonWorker.class) {
            if (Objects.nonNull(objectMapper)) {
                return objectMapper;
            }
            objectMapper = ObjectMapperWrapper.getObjectMapper();
        }
        return objectMapper;
    }

    public static <T> T readValue(String value, Class<T> valueType) {
        return tryCatch(() -> objectMapper.readValue(value, valueType));
    }

    public static <T> T readValue(String value, TypeReference<T> valueTypeRef) {
        return tryCatch(() -> objectMapper.readValue(value, valueTypeRef));
    }

    public static String writeValueAsString(Object value) {
        return tryCatch(() -> objectMapper.writeValueAsString(value));
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return tryCatch(() -> objectMapper.convertValue(fromValue, toValueType));
    }

    private static <T> T tryCatch(Callable<T> parser) {
        return tryCatch(parser, JacksonException.class);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> T tryCatch(Callable<T> parser, Class<? extends Exception> jsonException) {
        try {
            return parser.call();
        } catch (Exception e) {
            if (jsonException.isAssignableFrom(e.getClass())) {
                throw new CherryException(BaseExceptionEnum.JSON_ERROR, e.getStackTrace());
            }
            throw new CherryException(BaseExceptionEnum.SYSTEM_ERROR, e.getStackTrace());
        }
    }

}
