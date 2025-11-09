package com.pathfinder.user.application.excpetion;

import com.pathfinder.global.presentation.handler.GlobalExceptionHandler;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserErrorCode errorCode) {
        super();
    }
}
