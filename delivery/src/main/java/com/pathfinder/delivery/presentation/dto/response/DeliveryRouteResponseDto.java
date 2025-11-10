package com.pathfinder.delivery.presentation.dto.response;

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
public class DeliveryRouteResponseDto {
    private UUID routeId;
    private UUID deliveryId;
    private UUID fromHubId;
    private UUID toHubId;
    private Integer sequence;
    private String status;
    private LocalDateTime occurredAt;
    private Integer actualTime;
    private BigDecimal actualDistance;
    private Integer expectedTime;
    private BigDecimal expectedDistance;
    private UUID deliveryManagerId;
    private String note;
}

