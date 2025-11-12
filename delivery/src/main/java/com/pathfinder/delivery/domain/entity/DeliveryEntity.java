package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.UUID;

@Entity
@Table(name = "p_delivery")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "from_hub_id")
    private UUID fromHubId;

    @Column(name = "to_hub_id")
    private UUID toHubId;

    @Column(name = "delivery_manager_id", nullable = false)
    private Long deliveryManagerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.READY;

    @Column(name = "expected_distance", precision = 6, scale = 2)
    private BigDecimal expectedDistance;

    @Column(name = "actual_distance", precision = 6, scale = 2)
    private BigDecimal actualDistance;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_slack_id")
    private String receiverSlackId;

    public boolean updateWithCommand(UpdateDeliveryCommandDto command, DeliveryStatus validatedStatus) {
        boolean statusChanged = updateStatusIfChanged(validatedStatus);
        updateDeliveryFields(command);
        return statusChanged;
    }

    private boolean updateStatusIfChanged(DeliveryStatus validatedStatus) {
        if (validatedStatus != null && this.status != validatedStatus) {
            this.status = validatedStatus;
            return true;
        }
        return false;
    }

    private void updateDeliveryFields(UpdateDeliveryCommandDto command) {
        updateIfNotNull(command.getExpectedDistance(), value -> this.expectedDistance = value);
        updateIfNotNull(command.getActualDistance(), value -> this.actualDistance = value);
        updateIfNotNull(command.getDeliveryAddress(), value -> this.deliveryAddress = value);
        updateIfNotNull(command.getReceiverName(), value -> this.receiverName = value);
        updateIfNotNull(command.getReceiverSlackId(), value -> this.receiverSlackId = value);
        updateIfNotNull(command.getFromHubId(), value -> this.fromHubId = value);
        updateIfNotNull(command.getToHubId(), value -> this.toHubId = value);
        updateIfNotNull(command.getDeliveryManagerId(), value -> this.deliveryManagerId = value);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public void cancel() {
        if (this.status != DeliveryStatus.CANCELLED) {
            this.status = DeliveryStatus.CANCELLED;
        }
    }
    
    public String determineUpdateEventType(boolean statusChanged) {
        return statusChanged ? "STATUS_CHANGED" : "UPDATED";
    }

    public DeliveryDto toDeliveryDto() {
        return DeliveryDto.builder()
            .deliveryId(this.deliveryId)
            .orderId(this.orderId)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .status(this.status)
            .expectedDistance(this.expectedDistance)
            .actualDistance(this.actualDistance)
            .deliveryAddress(this.deliveryAddress)
            .receiverName(this.receiverName)
            .receiverSlackId(this.receiverSlackId)
            .createdAt(this.getCreatedAt())
            .modifiedAt(this.getModifiedAt())
            .build();
    }
    
    public DeliveryEventDto toEvent(String eventType) {
        return DeliveryEventDto.builder()
            .deliveryId(this.deliveryId)
            .orderId(this.orderId)
            .status(this.status)
            .fromHubId(this.fromHubId)
            .toHubId(this.toHubId)
            .deliveryManagerId(this.deliveryManagerId)
            .expectedDistance(this.expectedDistance)
            .actualDistance(this.actualDistance)
            .occurredAt(java.time.LocalDateTime.now())
            .eventType(eventType)
            .build();
    }
}
