package com.pathfinder.user.application.excpetion;

public class ValidationException  extends RuntimeException {

    public ValidationException(UserErrorCode errorCode, String missingValue) {
        super();
    }
}