package com.cherry.base.domain.model;

import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年11月26日 16:35:00
 * ClassName SystemEnvModel
 * packageName com.cherry.animal.base.domain.model
 */
@Data
@Slf4j
@Builder
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class SystemEnvModel {

    private final static String LOCAL = "local";

    private final static String DEV = "dev";

    private final static String PROD = "prod";

    public final static String DEV_IP = "124.221.143.138";

    @Value("${cherry.env}")
    private String env;

    public boolean localFlag() {
        return LOCAL.equalsIgnoreCase(env);
    }

    public boolean devFlag() {
        return DEV.equalsIgnoreCase(env);
    }

    public boolean prodFlag() {
        return PROD.equalsIgnoreCase(env);
    }

    @PostConstruct
    private void initEnv() {
        log.info("server start success, current env: {}", env);
    }
}
