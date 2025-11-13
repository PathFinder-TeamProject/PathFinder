package com.pathfinder.delivery_manager.infrastructure.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI DeliveryManagerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Manager Service API")
                        .description("배송담당자 서비스 API 문서")
                        .version("v1.0.0"));
    }
}
