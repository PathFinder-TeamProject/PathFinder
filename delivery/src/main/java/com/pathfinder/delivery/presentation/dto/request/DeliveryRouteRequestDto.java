package com.pathfinder.delivery.presentation.dto.request;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
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
public class DeliveryRouteRequestDto {
    private UUID fromHubId;
    private UUID toHubId;
    private DeliveryRouteStatus status;
    private LocalDateTime occurredAt;
    private Integer expectedTime;
    private BigDecimal expectedDistance;
    private Integer actualTime;
    private BigDecimal actualDistance;
    private UUID deliveryManagerId;
    private String note;

    public CreateDeliveryRouteCommandDto toCommand(UUID deliveryId) {
        return CreateDeliveryRouteCommandDto.builder()
            .deliveryId(deliveryId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .status(this.status)
            .occurredAt(this.occurredAt)
            .expectedTime(this.expectedTime)
            .expectedDistance(this.expectedDistance)
            .actualTime(this.actualTime)
            .actualDistance(this.actualDistance)
            .deliveryManagerId(this.deliveryManagerId)
            .note(this.note)
            .build();
    }
}

