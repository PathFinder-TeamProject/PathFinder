package com.pathfinder.delivery.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteDto {
    private UUID routeId;
    private Integer durationMin;
    private Double distanceKm;
    private UUID originHubId;
    private UUID destinationHubId;
    
    public Integer getTime() {
        return durationMin;
    }
    
    public Double getDistance() {
        return distanceKm;
    }
    
    public UUID getDepart() {
        return originHubId;
    }
    
    public UUID getArrive() {
        return destinationHubId;
    }
}

