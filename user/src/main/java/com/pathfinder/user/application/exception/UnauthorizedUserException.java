package com.pathfinder.user.application.exception;

public class UnauthorizedUserException extends RuntimeException {

    public UnauthorizedUserException(UserErrorCode errorCode) {
        super();
    }
}
