package com.pathfinder.delivery.presentation.dto.request;

import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
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
public class UpdateDeliveryRequestDto {
    private String status;
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
    
    public UpdateDeliveryCommandDto toCommand(UUID deliveryId) {
        String normalizedStatus = this.status != null ? DeliveryStatus.valueOf(this.status).name() : null;

        return UpdateDeliveryCommandDto.builder()
            .deliveryId(deliveryId)
            .status(normalizedStatus)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .expectedDistance(this.expectedDistance)
            .actualDistance(this.actualDistance)
            .deliveryAddress(this.deliveryAddress)
            .receiverName(this.receiverName)
            .receiverSlackId(this.receiverSlackId)
            .build();
    }
}
