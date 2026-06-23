package com.cherry.core.configuration.doc;

import com.cherry.core.properties.SpringDocProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : ganxiongwen
 * Date: 2022/4/21 15:40
 * Description: swagger 配置管理类
 * ClassName: SwaggerAutoConfiguration
 * Package: com.cherry.flow.common.config.swagger
 * Copyright (c) 2022,All Rights Reserved.
 */
@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(description = "local", url = "http://127.0.0.1:8787"),
                @Server(description = "dev", url = "https://cherry-coast.com:8787")
        }
)
@RequiredArgsConstructor
@ConditionalOnBean(SpringDocProperties.class)
public class SpringDocAutoConfiguration {

    private final SpringDocProperties springDocProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(springDocProperties.getTitle())
                                .description(springDocProperties.getDescription())
                                .version(springDocProperties.getVersion())
                                .contact(
                                        new Contact()
                                                .name(springDocProperties.getName())
                                                .url(springDocProperties.getUrl())
                                                .email(springDocProperties.getEmail())
                                )
                );
    }

}
