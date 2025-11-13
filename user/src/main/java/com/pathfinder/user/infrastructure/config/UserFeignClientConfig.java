package com.pathfinder.user.infrastructure.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class UserFeignClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options requestOptions() {
        int connectTimeoutMillis = 5000;
        int readTimeoutMillis = 5000;
        return new Request.Options(connectTimeoutMillis, readTimeoutMillis);
    }

    @Bean
    public Retryer retryer() {
        // 1초 간격으로 시작, 최대 3초까지, 최대 3회 재시도
        return new Retryer.Default(1000, 3000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            switch (response.status()) {
                case 404:
                    return new DeliveryManagerNotFoundException("배송담당자를 찾을 수 없습니다.");
                case 500:
                    return new DeliveryManagerServiceException("Delivery Manager Service 내부 오류가 발생했습니다.");
                default:
                    return new DeliveryManagerServiceException("Delivery Manager Service 호출 중 오류가 발생했습니다.");
            }
        };
    }

    public static class DeliveryManagerNotFoundException extends RuntimeException {
        public DeliveryManagerNotFoundException(String message) {
            super(message);
        }
    }

    public static class DeliveryManagerServiceException extends RuntimeException {
        public DeliveryManagerServiceException(String message) {
            super(message);
        }
    }
}