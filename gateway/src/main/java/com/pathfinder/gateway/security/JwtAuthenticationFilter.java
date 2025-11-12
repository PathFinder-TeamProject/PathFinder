package com.pathfinder.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    // 인증이 필요 없는 Public 경로 목록
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/v3/api-docs"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                DataBufferUtils.join(exchange.getRequest().getBody())
                        .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            String path = exchange.getRequest().getURI().getPath();
                            String bodyString = new String(bytes, StandardCharsets.UTF_8);

                            log.debug("=== JWT Filter 실행 ===");
                            log.debug("요청 경로: {}", path);
                            log.debug("Request Body: {}", bodyString);

                            // Public 경로는 JWT 검증 건너뜀
                            if (isPublicPath(path)) {
                                log.debug("Public 경로 - JWT 검증 건너뜀");
                                return chain.filter(rebuildExchange(exchange, bytes));
                            }

                            // JWT 토큰 검증
                            String token = jwtUtil.resolveToken(exchange.getRequest());
                            log.debug("추출된 토큰: {}", token != null ? "존재" : "없음");

                            if (token == null || !jwtUtil.validateToken(token)) {
                                log.warn("JWT 검증 실패 - 401 반환");
                                return onError(exchange, "Invalid JWT", HttpStatus.UNAUTHORIZED);
                            }

                            String username = jwtUtil.getUsername(token);
                            String role = jwtUtil.getRole(token);
                            log.debug("JWT 검증 성공 - 사용자: {}, 역할: {}", username, role);

                            // 헤더 추가
                            ServerWebExchange modifiedExchange = rebuildExchange(exchange, bytes)
                                    .mutate()
                                    .request(builder -> builder
                                            .header("X-User-Username", username)
                                            .header("X-User-Role", role))
                                    .build();

                            return chain.filter(modifiedExchange);
                        });
    }

    /** body 복사 후 재생성하는 메서드 */
    private ServerWebExchange rebuildExchange(ServerWebExchange exchange, byte[] bodyBytes) {
        Flux<DataBuffer> cachedBodyFlux = Flux.defer(() ->
                Mono.just(exchange.getResponse().bufferFactory().wrap(bodyBytes))
        );

        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return cachedBodyFlux;
            }
        };

        return exchange.mutate().request(mutatedRequest).build();
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
