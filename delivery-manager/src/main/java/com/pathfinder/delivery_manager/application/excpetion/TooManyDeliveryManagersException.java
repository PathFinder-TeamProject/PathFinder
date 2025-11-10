package com.pathfinder.delivery_manager.application.excpetion;

public class TooManyDeliveryManagersException extends RuntimeException {
    public TooManyDeliveryManagersException(DeliveryManagerErrorCode errorCode) {
        super();
    }
}
