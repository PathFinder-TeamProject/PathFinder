package com.pathfinder.delivery.presentation.dto.response;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
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
public class DeliveryResponse {
    private UUID deliveryId;
    private UUID orderId;
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private String status;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
    private Instant createdAt;
    private Instant modifiedAt;
    
    public static DeliveryResponse fromDeliveryDto(DeliveryDto dto) {
        return DeliveryResponse.builder()
            .deliveryId(dto.getDeliveryId())
            .orderId(dto.getOrderId())
            .fromHubId(dto.getFromHubId())
            .toHubId(dto.getToHubId())
            .deliveryManagerId(dto.getDeliveryManagerId())
            .status(dto.getStatus().name())
            .expectedDistance(dto.getExpectedDistance())
            .actualDistance(dto.getActualDistance())
            .deliveryAddress(dto.getDeliveryAddress())
            .receiverName(dto.getReceiverName())
            .receiverSlackId(dto.getReceiverSlackId())
            .createdAt(dto.getCreatedAt())
            .modifiedAt(dto.getModifiedAt())
            .build();
    }
}
