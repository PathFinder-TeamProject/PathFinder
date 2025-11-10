package com.pathfinder.order.presentation.dto.response;

import com.pathfinder.order.presentation.enums.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponseDto<T> {
    private String status;
    private String message;
    private T data;


    public ApiResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static <T> ApiResponseDto<T> success(ApiStatus status, T data, String message) {
        return new ApiResponseDto<>(status.name(), message, data);
    }

    public static <T> ApiResponseDto<T> success(ApiStatus status, String message) {
        return new ApiResponseDto<>(status.name(), message);
    }

    public static <T> ApiResponseDto<T> error(ApiStatus status, String message) {
        return new ApiResponseDto<>(status.name(), message, null);
    }
}
