package com.pathfinder.delivery_manager.application.excpetion;

public class DeliveryManagerNotFoundException extends RuntimeException {
    public DeliveryManagerNotFoundException(DeliveryManagerErrorCode errorCode) {
        super();
    }
}
