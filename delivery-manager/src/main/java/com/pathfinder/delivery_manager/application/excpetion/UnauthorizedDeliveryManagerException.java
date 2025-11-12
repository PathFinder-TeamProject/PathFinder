package com.pathfinder.delivery_manager.application.excpetion;
import com.pathfinder.delivery_manager.application.excpetion.DeliveryManagerErrorCode;

public class UnauthorizedDeliveryManagerException extends RuntimeException {

    public UnauthorizedDeliveryManagerException(DeliveryManagerErrorCode errorCode) {
        super();
    }
}
