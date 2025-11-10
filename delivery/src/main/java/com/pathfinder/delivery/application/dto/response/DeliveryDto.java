package com.pathfinder.delivery.application.dto.response;

import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.presentation.dto.response.DeliveryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private UUID deliveryId;
    private UUID orderId;
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private DeliveryStatus status;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
    private Instant createdAt;
    private Instant modifiedAt;

    public DeliveryResponseDto toResponseDto() {
        return DeliveryResponseDto.builder()
            .deliveryId(this.deliveryId)
            .orderId(this.orderId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .status(this.status.name())
            .expectedDistance(this.expectedDistance)
            .actualDistance(this.actualDistance)
            .deliveryAddress(this.deliveryAddress)
            .receiverName(this.receiverName)
            .receiverSlackId(this.receiverSlackId)
            .createdAt(this.createdAt)
            .modifiedAt(this.modifiedAt)
            .build();
    }
}
