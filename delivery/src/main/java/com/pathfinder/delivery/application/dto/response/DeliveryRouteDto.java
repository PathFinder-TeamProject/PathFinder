package com.pathfinder.delivery.application.dto.response;

import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
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
public class DeliveryRouteDto {
    private UUID routeId;
    private UUID deliveryId;
    private UUID fromHubId;
    private UUID toHubId;
    private Integer sequence;
    private DeliveryRouteStatus status;
    private LocalDateTime occurredAt;
    private Integer actualTime;
    private BigDecimal actualDistance;
    private Integer expectedTime;
    private BigDecimal expectedDistance;
    private UUID deliveryManagerId;
    private String note;

    public static DeliveryRouteDto fromDeliveryRouteEntity(DeliveryRouteEntity entity) {
        return DeliveryRouteDto.builder()
            .routeId(entity.getRouteId())
            .deliveryId(entity.getDeliveryId())
            .fromHubId(entity.getFromHubId())
            .toHubId(entity.getToHubId())
            .sequence(entity.getSequence())
            .status(entity.getStatus())
            .occurredAt(entity.getOccurredAt())
            .actualTime(entity.getActualTime())
            .actualDistance(entity.getActualDistance())
            .expectedTime(entity.getExpectedTime())
            .expectedDistance(entity.getExpectedDistance())
            .deliveryManagerId(entity.getDeliveryManagerId())
            .note(entity.getNote())
            .build();
    }
}

