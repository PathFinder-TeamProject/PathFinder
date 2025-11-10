package com.pathfinder.user.application.exception;

public class NotActiveUserException extends RuntimeException {
   public NotActiveUserException(UserErrorCode errorCode) {
        super();
    }
}
