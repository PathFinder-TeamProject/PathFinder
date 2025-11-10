package com.pathfinder.delivery_manager.infrastructure.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class FeignClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // NONE, BASIC, HEADERS, FULL
    }

    /**
     * 타임아웃 설정
     */
    @Bean
    public Request.Options requestOptions() {
        int connectTimeoutMillis = 5000;  // 연결 타임아웃: 5초
        int readTimeoutMillis = 5000;     // 읽기 타임아웃: 5초
        return new Request.Options(connectTimeoutMillis, readTimeoutMillis);
    }

    /**
     * 재시도 정책 설정
     */
    @Bean
    public Retryer retryer() {
        // 100ms 간격으로 시작, 최대 1초까지, 최대 3회 재시도
        return new Retryer.Default(100, 1000, 3);
    }

    /**
     * 에러 디코더 설정
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error("[Feign Client] User Service 호출 실패 - Status: {}, Reason: {}",
                    response.status(), response.reason());

            switch (response.status()) {
                case 404:
                    return new UserNotFoundException("사용자를 찾을 수 없습니다.");
                case 500:
                    return new UserServiceException("User Service 내부 오류가 발생했습니다.");
                default:
                    return new UserServiceException("User Service 호출 중 오류가 발생했습니다.");
            }
        };
    }

    // 커스텀 예외 클래스들
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserServiceException extends RuntimeException {
        public UserServiceException(String message) {
            super(message);
        }
    }
}