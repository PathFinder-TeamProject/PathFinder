package com.hub_service.domain.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hubs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID hubId;

    @Column(name = "name", nullable = false)
    private String hubName;

    @Column(name = "address", nullable = false)
    private String hubAddress;


    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, String address, Double lat, Double lon) {
        this.hubName = name;
        this.hubAddress = address;
        this.latitude = lat;
        this.longitude = lon;
    }


    public void softDelete(String username) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = username;
    }
}
