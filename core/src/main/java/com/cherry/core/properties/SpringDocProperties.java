package com.cherry.core.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : ganxiongwen
 * Date: 2022/4/21 15:40
 * Description: Swagger配置实体
 * ClassName: SwaggerProperties
 * Package: com.cherry.flow.common.config.swagger
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Component
@ConfigurationProperties(prefix = "cherry.api.doc")
public class SpringDocProperties {

    /**
     * 包扫描的路径
     */
    private String basePackage;

    /**
     * 联系人的名称
     */
    private String name;

    /**
     * 联系人的主页
     */
    private String url;

    /**
     * 联系人的邮箱
     */
    private String email;

    /**
     * API的标题
     */
    private String title;

    /**
     * API的描述
     */
    private String description;

    /**
     * API的版本号
     */
    private String version;

    /**
     * API的服务团队
     */
    private String termsOfServiceUrl;

}
