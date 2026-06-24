package com.cherry.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cherry.oss")
public class OssProperties {

    /**
     * OSS Endpoint
     */
    private String endpoint;

    /**
     * Access Key ID
     */
    private String accessKeyId;

    /**
     * Access Key Secret
     */
    private String accessKeySecret;

    /**
     * Bucket Name
     */
    private String bucketName;

    /**
     * 自定义域名 (可选)
     */
    private String domain;
}
