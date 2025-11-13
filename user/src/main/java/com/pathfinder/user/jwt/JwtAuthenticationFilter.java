package com.pathfinder.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.user.application.dto.request.LoginRequestDto;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import com.pathfinder.user.domain.repository.UserRepository;
import com.pathfinder.user.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/v1/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            log.info("=== 로그인 시도 시작 ===");

            LoginRequestDto requestDto = new ObjectMapper().readValue(
                    request.getInputStream(),
                    LoginRequestDto.class
            );

            log.info("로그인 요청 사용자: {}", requestDto.getUsername());

            // 사용자 조회 및 삭제 여부 확인
            UserEntity user = userRepository
                    .findByUsernameAndDeletedAtIsNull(requestDto.getUsername())
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없음: {}", requestDto.getUsername());
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                    });

            log.info("사용자 조회 성공: {}, 상태: {}", user.getUsername(), user.getStatus());

            // 승인 상태 확인
            if (!user.getStatus().equals(UserStatusEnum.APPROVED)) {
                log.warn("승인되지 않은 사용자 로그인 시도: {}", user.getUsername());
                throw new BadCredentialsException("승인되지 않은 사용자입니다.");
            }

            // AuthenticationManager 확인
            AuthenticationManager authManager = getAuthenticationManager();
            if (authManager == null) {
                log.error("AuthenticationManager가 null입니다!");
                throw new RuntimeException("AuthenticationManager가 초기화되지 않았습니다.");
            }

            log.info("인증 시도 중...");

            // 인증 객체 생성 및 인증 진행
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),  // user.getUsername() 대신 requestDto 사용
                            requestDto.getPassword(),
                            null  // authorities는 null로, 인증 후 자동으로 설정됨
                    )
            );

            log.info("인증 성공: {}", requestDto.getUsername());
            return authentication;

        } catch (IOException e) {
            log.error("요청 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("요청을 처리할 수 없습니다: " + e.getMessage(), e);
        } catch (AuthenticationException e) {
            log.error("인증 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        log.info("=== 인증 성공 처리 ===");
        UserEntity user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        String token = jwtUtil.createToken(user);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        log.info("JWT 토큰 생성 및 응답 헤더 추가 완료");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.error("=== 인증 실패 ===: {}", failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + failed.getMessage() + "\"}");
    }
}