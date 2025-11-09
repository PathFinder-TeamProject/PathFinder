package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_route")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "route_id")
    private UUID routeId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "from_hub_id")
    private UUID fromHubId;

    @Column(name = "to_hub_id")
    private UUID toHubId;

    @Column(name = "sequence")
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryRouteStatus status;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();

    @Column(name = "actual_time")
    private Integer actualTime;

    @Column(name = "actual_distance", precision = 6, scale = 2)
    private BigDecimal actualDistance;

    @Column(name = "expected_time")
    private Integer expectedTime;

    @Column(name = "expected_distance", precision = 6, scale = 2)
    private BigDecimal expectedDistance;

    @Column(name = "delivery_manager_id")
    private UUID deliveryManagerId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public void updateStatus(DeliveryRouteStatus status) {
        this.status = status;
        this.occurredAt = LocalDateTime.now();
    }

    public void updateActualMetrics(Integer actualTime, BigDecimal actualDistance) {
        this.actualTime = actualTime;
        this.actualDistance = actualDistance;
    }

    public void updateExpectedMetrics(Integer expectedTime, BigDecimal expectedDistance) {
        this.expectedTime = expectedTime;
        this.expectedDistance = expectedDistance;
    }

    /**
     * Entity를 DTO로 변환
     */
    public DeliveryRouteDto toDeliveryRouteDto() {
        return DeliveryRouteDto.fromDeliveryRouteEntity(this);
    }
}

