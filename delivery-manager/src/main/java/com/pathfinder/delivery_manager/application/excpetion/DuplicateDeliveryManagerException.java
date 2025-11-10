package com.pathfinder.delivery_manager.application.excpetion;


public class DuplicateDeliveryManagerException extends RuntimeException {

    public DuplicateDeliveryManagerException(DeliveryManagerErrorCode errorCode) {
        super();
    }
}
