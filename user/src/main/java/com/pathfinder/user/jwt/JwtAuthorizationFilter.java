package com.pathfinder.user.jwt;
import com.pathfinder.user.application.UserDetailsServiceImpl;
import com.pathfinder.user.application.exception.UserErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        log.info("=== JWT Authorization Filter 실행 ===");
        log.info("요청 URI: {}", requestURI);

        // /v1/auth/** 경로는 JWT 검증 건너뛰기
        if (requestURI.startsWith("/api/v1/auth/")) {
            log.info("인증 경로 - JWT 검증 건너뜀");
            filterChain.doFilter(request, response);
            return;
        }

        // Gateway에서 전달한 헤더 확인 (Gateway를 통한 요청인 경우)
        String gatewayUsername = request.getHeader("X-User-Username");
        String gatewayRole = request.getHeader("X-User-Role");
        if (StringUtils.hasText(gatewayUsername) && StringUtils.hasText(gatewayRole)) {
            // Gateway가 이미 JWT를 검증했으므로, 헤더 정보로 인증 설정
            log.debug("Gateway에서 검증된 사용자: {}, 역할: {}", gatewayUsername, gatewayRole);
            setAuthenticationFromGateway(gatewayUsername, gatewayRole);
            filterChain.doFilter(request, response);
            return;
        }

        // 직접 요청인 경우 JWT 토큰 검증
        String tokenValue = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(tokenValue)) {
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(UserErrorCode.INVALID_TOKEN.getMessage());
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // Gateway 헤더 기반 인증 설정
    private void setAuthenticationFromGateway(String username, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // ROLE_ 접두사 확인 및 추가
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 기존 JWT 토큰 기반 인증 설정
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}