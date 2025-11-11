package com.pathfinder.order.infrastructure.global.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pathfinder.order.infrastructure.global.security.auth.CustomUserDetails;
import com.pathfinder.order.infrastructure.global.security.jwt.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        log.info("=== JWT Authorization Filter (Delivery-Manager) ===");
        log.info("요청 URI: {}", requestURI);

        // Gateway에서 전달한 헤더 확인
        String gatewayUsername = request.getHeader("X-User-Username");
        String gatewayRole = request.getHeader("X-User-Role");

        log.info("Gateway 헤더 - Username: {}, Role: {}", gatewayUsername, gatewayRole);

        if (StringUtils.hasText(gatewayUsername) && StringUtils.hasText(gatewayRole)) {
            // Gateway가 이미 JWT를 검증했으므로, 헤더 정보로 인증 설정
            log.info("Gateway 검증 사용 - 사용자: {}, 역할: {}", gatewayUsername, gatewayRole);
            setAuthenticationFromGateway(gatewayUsername, gatewayRole);
        } else {
            log.warn("Gateway 헤더 없음 - 인증되지 않은 요청");
        }

        filterChain.doFilter(request, response);
    }

    // Gateway 헤더 기반 인증 설정
    private void setAuthenticationFromGateway(String username, String role) {
        log.info("Gateway 인증 설정 시작 - Username: {}, Role: {}", username, role);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // ROLE_ 접두사 확인 및 추가
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        log.info("최종 Authority: {}", authority);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        log.info("인증 설정 완료 - Principal: {}, Authorities: {}",
                authentication.getPrincipal(),
                authentication.getAuthorities());
    }
}