package com.pathfinder.gateway.filter;
import com.pathfinder.gateway.dto.UserResponseDto;
import com.pathfinder.gateway.security.JwtUtil;
import com.pathfinder.gateway.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = header.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return chain.filter(exchange);
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        UserResponseDto userInfo = userCacheService.getUser(userId);

        exchange = exchange.mutate()
                .request(req -> req.headers(h -> {
                    h.add("X-User-Username", userInfo.getUsername());
                    h.add("X-User-Role", userInfo.getRole());
                }))
                .build();

        return chain.filter(exchange);
    }
}