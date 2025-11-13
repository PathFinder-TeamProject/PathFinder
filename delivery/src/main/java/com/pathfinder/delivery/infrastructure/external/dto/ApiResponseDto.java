package com.pathfinder.delivery.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * order-service의 ApiResponseDto 구조에 맞춘 DTO
 * order-service는 status, message, data 필드를 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private String status;  // order-service는 status 필드 사용 (공통 ApiResponse는 code 사용)
    private String message;
    private T data;
}

