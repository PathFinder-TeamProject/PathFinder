package com.pathfinder.user.application.exception;

public class ValidationException  extends RuntimeException {

    public ValidationException(UserErrorCode errorCode, String missingValue) {
        super();
    }
}