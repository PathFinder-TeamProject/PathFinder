package com.pathfinder.user.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserErrorCode errorCode) {
        super();
    }
}
