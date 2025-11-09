package com.pathfinder.user.application.excpetion;

import com.pathfinder.global.presentation.handler.GlobalExceptionHandler;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException(UserErrorCode errorCode) {
        super();
    }
}
