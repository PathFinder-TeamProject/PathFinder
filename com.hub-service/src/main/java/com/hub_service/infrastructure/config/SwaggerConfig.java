package com.hub_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hubServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hub Service API")
                        .description("허브 서비스 API 문서")
                        .version("v1.0.0"));
    }
}
