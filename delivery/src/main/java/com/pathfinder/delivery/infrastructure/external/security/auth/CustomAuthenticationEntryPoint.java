package com.pathfinder.delivery.infrastructure.external.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.global.presentation.error.CommonErrorCode;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(CommonErrorCode.INVALID_INPUT.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(objectMapper.writeValueAsString(
                ApiResponse.fail(
                    CommonErrorCode.INVALID_INPUT.getCode(),
                    "로그인이 필요한 서비스 입니다.")
        ));
    }
}
