package com.pathfinder.order.application.exception;

import com.pathfinder.order.presentation.dto.response.ApiResponseDto;
import com.pathfinder.order.presentation.enums.ApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getStatus().getCode())
                .body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleException(Exception e) {
        e.printStackTrace(); // 로그용
        return ResponseEntity
                .status(ApiStatus.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponseDto.error(ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}