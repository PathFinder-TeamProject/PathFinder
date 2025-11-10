package com.hub_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hub_routes",
        indexes = { @Index(name = "idx_origin_dest", columnList = "origin_hub_id, destination_hub_id") })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HubRoute {

    @Id
    @Column(name = "route_id", nullable = false, updatable = false)
    private UUID routeId;

    @Column(name = "origin_hub_id", nullable = false)
    private UUID originHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    // km 단위 거리
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    // 분 단위 소요 시간
    @Column(name = "duration_min", nullable = false)
    private Integer durationMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", length = 32)
    private RouteType routeType;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum RouteType {
        P2P, HUB_RELAY
    }

    @PrePersist
    public void prePersist() {
        if (routeId == null) routeId = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
