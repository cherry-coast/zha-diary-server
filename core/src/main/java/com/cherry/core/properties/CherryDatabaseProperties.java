package com.cherry.core.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cherry
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "cherry.database")
public class CherryDatabaseProperties {

    /**
     * system env
     */
    @Value("${sqlLogEnable:false}")
    private boolean sqlLogEnable;

    /**
     * system env
     */
    @Value("${sqlExecTime:3000}")
    private long sqlExecTime;

}
