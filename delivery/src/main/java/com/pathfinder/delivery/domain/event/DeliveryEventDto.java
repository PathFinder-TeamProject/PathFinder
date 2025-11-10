package com.pathfinder.delivery.domain.event;

import com.pathfinder.delivery.domain.entity.DeliveryOutboxEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.enums.DeliveryOutboxStatus;
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
public class DeliveryEventDto {
    private UUID deliveryId;
    private UUID orderId;
    private DeliveryStatus status;
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private LocalDateTime occurredAt;
    private String eventType;

    public DeliveryOutboxEntity toOutboxEntity(String payload) {
        return DeliveryOutboxEntity.builder()
            .aggregateType("DELIVERY")
            .aggregateId(this.deliveryId)
            .eventType(this.eventType)
            .payload(payload)
            .status(DeliveryOutboxStatus.PENDING)
            .build();
    }
}

