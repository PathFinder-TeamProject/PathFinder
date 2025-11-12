package com.pathfinder.delivery_manager.infrastructure.config;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.pathfinder.delivery_manager.infrastructure.security.JwtAuthorizationFilter;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // DeliveryManager 권한 설정
                        .requestMatchers("/v3/api-docs/**", "/internal/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/v1/delivery-managers",
                                "/v1/delivery-managers/{delivery_manager_id}")
                        .hasAnyRole("DELIVERY_MANAGER", "HUB_MANAGER", "MASTER")

                        // 그 외 모든 작업: HUB_MANAGER, MASTER만 가능
                        .requestMatchers("/v1/delivery-managers/**")
                        .hasAnyRole("HUB_MANAGER", "MASTER")

                        .anyRequest().authenticated()
        );

        // Gateway 헤더 기반 인증 필터 추가
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}