package com.pathfinder.delivery.presentation.dto.response;

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
public class DeliveryResponseDto {
    private UUID deliveryId;
    private UUID orderId;
    private UUID fromHubId;
    private UUID toHubId;
    private Long deliveryManagerId;
    private String status;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
    private Instant createdAt;
    private Instant modifiedAt;
}
