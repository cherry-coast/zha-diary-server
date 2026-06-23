package com.cherry.core.configuration.jackson;

import com.cherry.base.utils.CherryDateUtil;
import com.cherry.base.worker.CherryJsonWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author cherry
 */
@Configuration
public class JacksonConfiguration {

    /**
     * configure the object serialization container.
     *
     * @return object serialization container
     */
    @Bean
    public ObjectMapper objectMapper() {
        return CherryJsonWorker.getInstance();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 配置 LocalDateTime 的序列化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CherryDateUtil.CherryDatePattern.NORM_DATETIME_PATTERN);
            LocalDateTimeSerializer serializer = new LocalDateTimeSerializer(formatter);
            builder.serializerByType(LocalDateTime.class, serializer);
        };
    }
}
