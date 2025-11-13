package com.pathfinder.delivery.application.dto.response;

import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import com.pathfinder.delivery.presentation.dto.response.DeliveryRouteResponseDto;
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
    private Long deliveryManagerId;
    private String note;

    public DeliveryRouteResponseDto toResponseDto() {
        return DeliveryRouteResponseDto.builder()
            .routeId(this.routeId)
            .deliveryId(this.deliveryId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .sequence(this.sequence)
            .status(this.status != null ? this.status.name() : null)
            .occurredAt(this.occurredAt)
            .actualTime(this.actualTime)
            .actualDistance(this.actualDistance)
            .expectedTime(this.expectedTime)
            .expectedDistance(this.expectedDistance)
            .deliveryManagerId(this.deliveryManagerId)
            .note(this.note)
            .build();
    }
}

