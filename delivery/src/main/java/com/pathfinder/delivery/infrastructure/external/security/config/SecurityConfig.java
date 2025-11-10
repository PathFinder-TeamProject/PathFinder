package com.pathfinder.delivery.infrastructure.external.security.config;

import com.pathfinder.delivery.infrastructure.external.security.auth.CustomAccessDeniedHandler;
import com.pathfinder.delivery.infrastructure.external.security.auth.CustomAuthenticationEntryPoint;
import com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Nullable
    @Value("${spring.cloud.config.profile:local}")
    private String activeProfile;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> {
                if ("dev".equalsIgnoreCase(activeProfile)) {
                    authorize.requestMatchers("/h2/**").permitAll();
                } else {
                    authorize.requestMatchers("/h2/**").denyAll();
                }

                authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                authorize.requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/springdoc/**",
                    "/favicon.ico",
                    "/docs/**",
                    "/swagger-ui/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll();
                authorize.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll();
                authorize.requestMatchers("/actuator/**").hasRole("MASTER");
                authorize.anyRequest().authenticated();
            });

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowedOriginPatterns(List.of(
                "http://127.0.0.1:[*]",
                "http://localhost:[*]"
            ));
            configuration.setAllowCredentials(true);
            return configuration;
        };
    }
}
