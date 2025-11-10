package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.global.presentation.exception.PathException;
import org.springframework.stereotype.Component;


@Component
public class DeliveryStatusValidator {

    public void validateStatusTransition(DeliveryStatus currentStatus, DeliveryStatus newStatus) {
        switch (currentStatus) {
            case READY:
                if (newStatus != DeliveryStatus.IN_PROGRESS && newStatus != DeliveryStatus.CANCELLED) {
                    throw new PathException(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
                }
                break;
            case IN_PROGRESS:
                if (newStatus != DeliveryStatus.DONE && newStatus != DeliveryStatus.CANCELLED) {
                    throw new PathException(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
                }
                break;
            case DONE:
                throw new PathException(DeliveryErrorCode.DELIVERY_ALREADY_COMPLETED);
            case CANCELLED:
                throw new PathException(DeliveryErrorCode.DELIVERY_ALREADY_CANCELLED);
        }
    }
}

