package com.pathfinder.delivery_manager.infrastructure.config;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 기본 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // CSRF 방어 비활성화 (API 서버의 경우 일반적으로 비활성화)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 관리를 STATELESS로 설정 (JWT 기반 REST API 표준)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청 권한 설정: 모든 요청(anyRequest)을 허용(permitAll)하도록 설정
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests.anyRequest().permitAll()
                )

                // 기존 커스텀 필터(JwtAuthorizationFilter) 등록 및 관련 종속성을 제거합니다.

                .build();
    }
}