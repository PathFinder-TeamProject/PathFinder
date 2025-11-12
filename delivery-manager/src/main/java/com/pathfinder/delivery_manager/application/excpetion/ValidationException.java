package com.pathfinder.delivery_manager.application.excpetion;

public class ValidationException  extends RuntimeException {

    public ValidationException(DeliveryManagerErrorCode errorCode, String missingValue) {
        super();
    }
}