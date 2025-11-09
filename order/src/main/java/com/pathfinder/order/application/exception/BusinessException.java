package com.pathfinder.order.application.exception;

import com.pathfinder.order.presentation.enums.ApiStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ApiStatus status;

    public BusinessException(ApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BusinessException(ApiStatus status, String message) {
        super(message);
        this.status = status;
    }
}
