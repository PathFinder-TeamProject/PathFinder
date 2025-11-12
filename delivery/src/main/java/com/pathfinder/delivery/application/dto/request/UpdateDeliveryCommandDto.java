package com.pathfinder.delivery.application.dto.request;

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
public class UpdateDeliveryCommandDto {
    @NotNull
    private UUID deliveryId;
    
    private String status;
    private UUID fromHubId;
    private UUID toHubId;
    private Long deliveryManagerId;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
}
