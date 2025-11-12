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
            log.error("[Feign Client] Delivery Manager Service 호출 실패 - Method: {}, Status: {}, Reason: {}",
                    methodKey, response.status(), response.reason());

            switch (response.status()) {
                case 404:
                    log.warn("[Feign Client] 배송담당자를 찾을 수 없음 - 이미 삭제되었을 수 있음");
                    return new DeliveryManagerNotFoundException("배송담당자를 찾을 수 없습니다.");
                case 500:
                    log.error("[Feign Client] Delivery Manager Service 내부 오류");
                    return new DeliveryManagerServiceException("Delivery Manager Service 내부 오류가 발생했습니다.");
                default:
                    log.error("[Feign Client] 알 수 없는 오류 발생 - Status: {}", response.status());
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