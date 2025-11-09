package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private UUID deliveryManagerId;

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

    /**
     * 배달 상태 업데이트
     */
    public void updateStatus(DeliveryStatus status) {
        this.status = status;
    }

    /**
     * 배달 전체 정보 업데이트 (Command 패턴)
     */
    public void update(UpdateDeliveryCommand command) {
        if (command.getExpectedDistance() != null) {
            this.expectedDistance = command.getExpectedDistance();
        }
        if (command.getActualDistance() != null) {
            this.actualDistance = command.getActualDistance();
        }
        if (command.getDeliveryAddress() != null) {
            this.deliveryAddress = command.getDeliveryAddress();
        }
        if (command.getReceiverName() != null) {
            this.receiverName = command.getReceiverName();
        }
        if (command.getReceiverSlackId() != null) {
            this.receiverSlackId = command.getReceiverSlackId();
        }
        if (command.getFromHubId() != null) {
            this.fromHubId = command.getFromHubId();
        }
        if (command.getToHubId() != null) {
            this.toHubId = command.getToHubId();
        }
        if (command.getDeliveryManagerId() != null) {
            this.deliveryManagerId = command.getDeliveryManagerId();
        }
    }

    /**
     * 취소로 상태 변경
     */
    public void cancel() {
        if (this.status != DeliveryStatus.CANCELLED) {
            this.status = DeliveryStatus.CANCELLED;
        }
    }

    /**
     * Entity를 DTO로 변환
     */
    public DeliveryDto toDeliveryDto() {
        return DeliveryDto.fromDeliveryEntity(this);
    }
}
