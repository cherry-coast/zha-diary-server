package com.cherry.base.wrapper;

import com.cherry.base.domain.constant.DateConstant;
import com.cherry.base.utils.CherryDateUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年08月22日 11:27:00
 * ClassName ObjectMapperWrapper
 * packageName com.cherry.base.wrapper
 */
public class ObjectMapperWrapper {

    /**
     * configure the object serialization container.
     *
     * @return object serialization container
     */
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CherryDateUtil.CherryDatePattern.NORM_DATETIME_PATTERN);
        LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(formatter);
        module.addSerializer(LocalDateTime.class, serializer);
        objectMapper.registerModule(module);

        // the time zone is set to : Asia/Shanghai
        objectMapper.setTimeZone(TimeZone.getTimeZone(DateConstant.SHANG_HAI_TIME_ZONE));
        objectMapper.setDateFormat(new SimpleDateFormat(DateConstant.yyyyMMddHHmmss));
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

}
