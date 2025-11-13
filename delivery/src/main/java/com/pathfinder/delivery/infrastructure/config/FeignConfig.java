package com.pathfinder.delivery.infrastructure.config;

import com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Configuration
@EnableFeignClients(basePackages = "com.pathfinder.delivery.infrastructure.external")
public class FeignConfig {

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 현재 요청의 헤더에서 인증 정보 가져오기
                Optional<ServletRequestAttributes> requestAttributes =
                        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                                .filter(ServletRequestAttributes.class::isInstance)
                                .map(ServletRequestAttributes.class::cast);

                if (requestAttributes.isPresent()) {
                    HttpServletRequest request = requestAttributes.get().getRequest();

                    // 헤더에서 직접 가져오기
                    String username = request.getHeader(JwtAuthorizationFilter.USERNAME_HEADER);
                    String role = request.getHeader(JwtAuthorizationFilter.ROLE_HEADER);

                    // 헤더에 없으면 SecurityContext에서 가져오기
                    if (username == null || role == null) {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.getPrincipal() instanceof JwtAuthorizationFilter.GatewayPrincipal principal) {
                            username = principal.username();
                            role = principal.primaryRole().orElse(null);
                        }
                    }

                    // Feign 요청에 헤더 추가
                    if (username != null) {
                        template.header(JwtAuthorizationFilter.USERNAME_HEADER, username);
                        log.debug("Feign 요청에 헤더 추가 - {}: {}", JwtAuthorizationFilter.USERNAME_HEADER, username);
                    }
                    if (role != null) {
                        template.header(JwtAuthorizationFilter.ROLE_HEADER, role);
                        log.debug("Feign 요청에 헤더 추가 - {}: {}", JwtAuthorizationFilter.ROLE_HEADER, role);
                    }
                } else {
                    log.warn("RequestContextHolder에서 요청 정보를 가져올 수 없습니다. Feign 요청에 인증 헤더를 추가하지 않습니다.");
                }
            }
        };
    }
}



