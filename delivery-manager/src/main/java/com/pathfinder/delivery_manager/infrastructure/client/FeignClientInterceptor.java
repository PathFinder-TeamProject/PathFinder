package com.pathfinder.delivery_manager.infrastructure.client;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Feign Client 요청 인터셉터
 * 원본 요청의 JWT 토큰을 Feign 요청에 전달
 */
@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Authorization 헤더 전달
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                template.header("Authorization", authorization);
                log.debug("[Feign Interceptor] Authorization 헤더 전달: {}",
                        authorization.substring(0, Math.min(20, authorization.length())) + "...");
            }

            // 필요한 경우 다른 헤더도 전달
            String username = request.getHeader("X-User-Username");
            if (username != null) {
                template.header("X-User-Username", username);
            }

            String role = request.getHeader("X-User-Role");
            if (role != null) {
                template.header("X-User-Role", role);
            }
        }
    }
}