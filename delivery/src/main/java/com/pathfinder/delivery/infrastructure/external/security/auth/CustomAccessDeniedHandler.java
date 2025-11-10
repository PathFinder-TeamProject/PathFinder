package com.pathfinder.delivery.infrastructure.external.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.global.presentation.error.CommonErrorCode;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(CommonErrorCode.METHOD_NOT_ALLOWED.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(objectMapper.writeValueAsString(
                ApiResponse.fail(
                    CommonErrorCode.METHOD_NOT_ALLOWED.getCode(),
                    "권한이 없습니다.")
        ));
    }
}