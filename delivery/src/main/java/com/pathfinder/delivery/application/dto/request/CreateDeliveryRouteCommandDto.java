package com.pathfinder.delivery.application.dto.request;

import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRouteCommandDto {
    private UUID deliveryId;
    private UUID fromHubId;
    private UUID toHubId;
    private DeliveryRouteStatus status;
    private LocalDateTime occurredAt;
    private Integer expectedTime;
    private BigDecimal expectedDistance;
    private Integer actualTime;
    private BigDecimal actualDistance;
    private Long deliveryManagerId;
    private String note;

        public DeliveryRouteEntity toEntity(int sequence) {
        return DeliveryRouteEntity.builder()
            .deliveryId(this.deliveryId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .sequence(sequence)
            .status(this.status != null ? this.status : DeliveryRouteStatus.READY)
            .occurredAt(this.occurredAt != null ? this.occurredAt : LocalDateTime.now())
            .expectedTime(this.expectedTime)
            .expectedDistance(this.expectedDistance)
            .actualTime(this.actualTime)
            .actualDistance(this.actualDistance)
            .deliveryManagerId(this.deliveryManagerId)
            .note(this.note)
            .build();
    }

  
    public static DeliveryRouteEntity createRouteWithHubInfo(
        UUID deliveryId,
        UUID fromHubId,
        UUID toHubId,
        int sequence,
        Integer expectedTime,
        BigDecimal expectedDistance,
        Long deliveryManagerId
    ) {
        return DeliveryRouteEntity.builder()
            .deliveryId(deliveryId)
            .fromHubId(fromHubId)
            .toHubId(toHubId)
            .sequence(sequence)
            .status(DeliveryRouteStatus.READY)
            .occurredAt(LocalDateTime.now())
            .expectedTime(expectedTime)
            .expectedDistance(expectedDistance)
            .deliveryManagerId(deliveryManagerId)
            .build();
    }
}

