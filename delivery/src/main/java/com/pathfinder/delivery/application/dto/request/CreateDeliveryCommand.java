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
public class CreateDeliveryCommand {
    @NotNull
    private UUID orderId;
    
    private UUID fromHubId;
    private UUID toHubId;
    
    @NotNull
    private UUID deliveryManagerId;
    
    private BigDecimal expectedDistance;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;

    /**
     * Command를 Entity로 변환
     * 서비스 레이어의 Builder 코드 간소화
     */
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
