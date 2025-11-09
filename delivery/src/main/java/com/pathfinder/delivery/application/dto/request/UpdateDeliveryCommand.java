package com.pathfinder.delivery.application.dto.request;

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
public class UpdateDeliveryCommand {
    @NotNull
    private UUID deliveryId;
    
    private String status; // String으로 받아서 변환
    private UUID fromHubId;
    private UUID toHubId;
    private UUID deliveryManagerId;
    private BigDecimal expectedDistance;
    private BigDecimal actualDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
}
