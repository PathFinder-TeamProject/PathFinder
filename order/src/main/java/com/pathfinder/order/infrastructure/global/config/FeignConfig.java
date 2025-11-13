package com.pathfinder.order.infrastructure.global.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FeignConfig {

    public static final String USERNAME_HEADER = "X-User-Username";
    public static final String ROLE_HEADER = "X-User-Role";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attrs == null) {
                    log.warn("Feign 요청: RequestContext 없음 (스케줄러/비동기 요청일 수 있음)");
                    return;
                }

                HttpServletRequest request = attrs.getRequest();

                String username = request.getHeader(USERNAME_HEADER);
                String role = request.getHeader(ROLE_HEADER);

                log.info("[Feign Header Forward] username: {}, role: {}", username, role);

                if (username != null) {
                    template.header(USERNAME_HEADER, username);
                }
                if (role != null) {
                    template.header(ROLE_HEADER, role);
                }
            }
        };
    }

}
