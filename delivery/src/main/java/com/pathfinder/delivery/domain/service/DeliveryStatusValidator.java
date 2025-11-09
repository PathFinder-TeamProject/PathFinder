package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.global.presentation.exception.PathException;
import org.springframework.stereotype.Component;

/**
 * 배달 상태 전이 검증 Domain Service
 */
@Component
public class DeliveryStatusValidator {

    /**
     * 배달 상태 전이 가능 여부 검증
     * 
     * @param currentStatus 현재 상태
     * @param newStatus 변경하려는 상태
     * @throws PathException 유효하지 않은 상태 전이인 경우
     */
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

