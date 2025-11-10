package com.pathfinder.user.application.exception;


public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(UserErrorCode errorCode, String duplicateValue) {
        super();
    }
}
