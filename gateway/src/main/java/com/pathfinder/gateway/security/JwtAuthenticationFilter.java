package com.pathfinder.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    // 인증이 필요 없는 Public 경로 목록
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/v1/auth/register",
            "/v1/auth/login",
            "/v1/auth/logout",
            "/v3/api-docs"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            System.out.println("=== JWT Filter 실행 ===");
            System.out.println("요청 경로: " + path);

            // Public 경로는 JWT 검증 건너뛰기
            if (isPublicPath(path)) {
                System.out.println("Public 경로 - JWT 검증 건너뜀");
                return chain.filter(exchange);
            }

            // JWT 토큰 검증
            String token = jwtUtil.resolveToken(exchange.getRequest());
            System.out.println("추출된 토큰: " + (token != null ? "존재" : "없음"));

            if (token == null || !jwtUtil.validateToken(token)) {
                System.out.println("JWT 검증 실패 - 401 반환");
                return onError(exchange, "Invalid JWT", HttpStatus.UNAUTHORIZED);
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            System.out.println("JWT 검증 성공 - 사용자: " + username + ", 역할: " + role);

            // 헤더 추가하여 새로운 요청 생성
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Username", username)
                            .header("X-User-Role", role)
                    )
                    .build();

            System.out.println("헤더 전달 완료");
            return chain.filter(modifiedExchange);
        };
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {}
}