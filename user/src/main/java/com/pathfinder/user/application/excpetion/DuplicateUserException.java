package com.pathfinder.user.application.excpetion;


public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(UserErrorCode errorCode, String duplicateValue) {
        super();
    }
}
