package com.pathfinder.delivery.domain.event;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDomainEvent {
    private UUID deliveryId;
    private UUID orderId;
    private DeliveryStatus status;
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private LocalDateTime occurredAt;
    private String eventType; // CREATED, UPDATED, STATUS_CHANGED, CANCELLED

    /**
     * Entity로부터 이벤트 생성 (간소화)
     * 서비스 레이어의 Builder 코드 간소화
     */
    public static DeliveryDomainEvent from(DeliveryEntity entity, String eventType) {
        return DeliveryDomainEvent.builder()
            .deliveryId(entity.getDeliveryId())
            .orderId(entity.getOrderId())
            .status(entity.getStatus())
            .fromHubId(entity.getFromHubId())
            .toHubId(entity.getToHubId())
            .deliveryManagerId(entity.getDeliveryManagerId())
            .expectedDistance(entity.getExpectedDistance())
            .actualDistance(entity.getActualDistance())
            .occurredAt(LocalDateTime.now())
            .eventType(eventType)
            .build();
    }
}
