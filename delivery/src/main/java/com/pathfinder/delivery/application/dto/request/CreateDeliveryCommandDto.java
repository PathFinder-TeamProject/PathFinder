package com.pathfinder.delivery.application.dto.request;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryCommandDto {
    @NotNull
    private UUID orderId;
    
    private UUID fromHubId;
    private UUID toHubId;
    
    private Long deliveryManagerId;
    
    private BigDecimal expectedDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;

    public CreateDeliveryCommandDto withDeliveryManagerId(Long deliveryManagerId) {
        return CreateDeliveryCommandDto.builder()
                .orderId(this.orderId)
                .fromHubId(this.fromHubId)
                .toHubId(this.toHubId)
                .deliveryManagerId(deliveryManagerId)
                .expectedDistance(this.expectedDistance)
                .deliveryAddress(this.deliveryAddress)
                .receiverName(this.receiverName)
                .receiverSlackId(this.receiverSlackId)
                .build();
    }

    public DeliveryEntity toEntity(BigDecimal calculatedExpectedDistance) {
        return DeliveryEntity.builder()
            .orderId(this.orderId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .status(DeliveryStatus.READY)
            .expectedDistance(calculatedExpectedDistance != null ? calculatedExpectedDistance : this.expectedDistance)
            .deliveryAddress(this.deliveryAddress)
            .receiverName(this.receiverName)
            .receiverSlackId(this.receiverSlackId)
            .build();
    }
}
