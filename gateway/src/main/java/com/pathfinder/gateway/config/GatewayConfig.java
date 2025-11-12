package com.pathfinder.gateway.config;

import com.pathfinder.gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // 인증이 필요 없는 경로 (JWT 필터 제외)
                .route("user-auth-public", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .setStatus(200) // 디버깅용
                        )
                        .uri("lb://user-service"))

                // 인증이 필요한 유저 서비스 경로
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://user-service"))

                // Delivery Manager Service
                .route("delivery-manager-service", r -> r.path("/api/v1/delivery-managers/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://DELIVERY-MANAGER-SERVICE"))

                // Hub Service
/*                .route("hub-service", r -> r.path("/api/v1/hubs/**")
//                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://HUB-SERVICE"))*/
                .route("hub-service", r -> r
                        .path("/api/v1/hubs/**")
                        // 필터 제거
                        .uri("lb://HUB-SERVICE"))
                // Company Service
                .route("company-service", r -> r.path("/api/v1/companys/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://COMPANY-SERVICE"))

                // Delivery Service
                .route("delivery-service", r -> r.path("/api/v1/deliverys/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://DELIVERY-SERVICE"))

                // Product Service
                .route("product-service", r -> r.path("/api/v1/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))

                // Messages Service
                .route("messages-service", r -> r.path("/api/v1/slack-messages/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://MESSAGES-SERVICE"))

                // AI Service
                .route("ai-service", r -> r.path("/v1/ai/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://ai-SERVICE"))

                .build();
    }
}