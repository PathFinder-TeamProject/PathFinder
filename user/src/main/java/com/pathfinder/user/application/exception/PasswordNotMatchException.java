package com.pathfinder.user.application.exception;


public class PasswordNotMatchException extends RuntimeException {

    public PasswordNotMatchException(UserErrorCode errorCode) {
        super();
    }
}
