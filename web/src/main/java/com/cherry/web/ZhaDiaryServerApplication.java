package com.cherry.web;

import com.cherry.core.properties.SystemProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2026年06月23日 16:37:00
 * ClassName ZhaDiaryServerApplication
 * packageName com.cherry.web
 */
@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan("com.cherry.*")
@MapperScan("com.cherry.database.mapper")
public class ZhaDiaryServerApplication {

    public static void main(String[] args) {
        SystemProperties.init();
        SpringApplication.run(ZhaDiaryServerApplication.class, args);
    }

}
