package com.pathfinder.delivery_manager.application.excpetion;

public class NotActiveDeliveryManagerException extends RuntimeException {
   public NotActiveDeliveryManagerException(DeliveryManagerErrorCode errorCode) {
        super();
    }
}
