package com.pathfinder.user.application.excpetion;

public class NotActiveUser extends RuntimeException {
   public NotActiveUser(UserErrorCode errorCode) {
        super();
    }
}
