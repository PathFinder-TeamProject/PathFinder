package com.pathfinder.delivery.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local")
@EnableFeignClients(basePackages = "com.pathfinder.delivery.infrastructure.external")
public class FeignConfig {
}

