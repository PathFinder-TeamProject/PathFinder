package com.pathfinder.user.infrastructure.config.security;

import com.pathfinder.user.application.UserDetailsServiceImpl;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.jwt.JwtAuthenticationFilter;
import com.pathfinder.user.jwt.JwtAuthorizationFilter;
import com.pathfinder.user.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Http basic Auth 기반으로 로그인 인증창이 뜨지 않게 설정
        http.httpBasic(HttpBasicConfigurer::disable);

        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests

                        .requestMatchers("/v1/auth/**", "/v3/api-docs/**", "/internal/**").permitAll()
                        // user
                        .requestMatchers(HttpMethod.GET, "/v1/users/myInfo").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/users/*").authenticated()
                        .requestMatchers("/v1/users/**").hasRole("MASTER")

                        // DeliveryManager > 각 서비스로 이동
                      /*  .requestMatchers(HttpMethod.GET, "/v1/delivery-managers", "/v1/delivery-managers/{delivery_manager_id}").hasAnyRole("DELIVERY_MANAGER", "HUB_MANAGER", "MASTER")
                        .requestMatchers("/v1/delivery-managers/**").hasAnyRole("HUB_MANAGER", "MASTER")
*/
                        // AI
                        .requestMatchers("/api/v1/ai/**").hasRole("MASTER")
                        .requestMatchers(HttpMethod.POST, "/v1/ai").authenticated()

                        // Hubs
                        .requestMatchers(HttpMethod.POST,"/v1/hubs").hasRole("MASTER")
                        .requestMatchers(HttpMethod.PUT,"/v1/hubs/{hubId}").hasRole("MASTER")
                        .requestMatchers(HttpMethod.DELETE,"/v1/hubs/{hubId}").hasRole("MASTER")
                        .requestMatchers(HttpMethod.GET,"/v1/hubs","/v1/hubs/{hubId}")
                        .authenticated()

                        // Deliveries
                        // Deliveries API
                        .requestMatchers(HttpMethod.POST, "/v1/deliverys/*/assign").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/deliverys").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/v1/deliverys/*/routes/*").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/v1/deliverys/*/routes/*").hasAnyRole("MASTER", "HUB_MANAGER", "DELIVERY_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/deliverys/*").hasAnyRole("MASTER", "DELIVERY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/deliverys/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/deliverys/*/routes/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/deliverys/*/routes", "/v1/deliverys", "/v1/deliverys/*").authenticated()
                        // Slack Messages
                        .requestMatchers(HttpMethod.GET,"/v1/slack-messages","/v1/slack-messages/{message_id}").hasRole("MASTER")
                        .requestMatchers(HttpMethod.DELETE,"/v1/slack-messages/{message_id}").hasRole("MASTER")
                        .requestMatchers(HttpMethod.PATCH,"/v1/slack-messages/{message_id}").hasRole("MASTER")
                        .requestMatchers(HttpMethod.POST,"/v1/slack-messages")
                        .authenticated()

                        // Products
                        .requestMatchers(HttpMethod.GET,"/v1/products","/v1/products/{product_id}").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.POST,"/v1/products").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/v1/products/{product_id}").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/v1/products/{product_id}").hasAnyRole("MASTER", "HUB_MANAGER")

                        // Companies
                        .requestMatchers(HttpMethod.GET,"/v1/companys","/v1/companys/{company_id}")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,"/v1/companys").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/v1/companys/{company_id}").hasAnyRole("MASTER", "HUB_MANAGER", "COMPANY_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/v1/companys/{company_id}").hasAnyRole("MASTER", "HUB_MANAGER")

                        // Orders
                        .requestMatchers(HttpMethod.GET, "/v1/orders", "/v1/orders/{order_id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/orders","/v1/orders/{order_id}").hasAnyRole("MASTER", "HUB_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/orders/{order_id}").hasAnyRole("MASTER", "HUB_MANAGER")

                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );
        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}
