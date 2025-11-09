package com.pathfinder.user.application.excpetion;


import com.pathfinder.global.presentation.handler.GlobalExceptionHandler;

public class PasswordNotMatchException extends RuntimeException {

    public PasswordNotMatchException(UserErrorCode errorCode) {
        super();
    }
}
