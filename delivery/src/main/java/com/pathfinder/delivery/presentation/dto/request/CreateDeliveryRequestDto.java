package com.pathfinder.delivery.presentation.dto.request;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommandDto;
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
public class CreateDeliveryRequestDto {
    @NotNull
    private UUID orderId;
    
    private UUID fromHubId;
    private UUID toHubId;
    
    private UUID deliveryManagerId;
    
    private BigDecimal expectedDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
    
    public CreateDeliveryCommandDto toCommand() {
        return CreateDeliveryCommandDto.builder()
            .orderId(this.orderId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .expectedDistance(this.expectedDistance)
            .deliveryAddress(this.deliveryAddress)
            .receiverName(this.receiverName)
            .receiverSlackId(this.receiverSlackId)
            .build();
    }
}
