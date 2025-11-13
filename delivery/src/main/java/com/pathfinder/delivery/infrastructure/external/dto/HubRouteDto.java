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
    private Integer durationMin;  // time -> durationMin (hub-service의 HubRoute와 매핑)
    private Double distanceKm;  // distance -> distanceKm (hub-service의 HubRoute와 매핑)
    private UUID originHubId;  // depart -> originHubId (hub-service의 HubRoute와 매핑)
    private UUID destinationHubId;  // arrive -> destinationHubId (hub-service의 HubRoute와 매핑)
    
    /**
     * 기존 필드명과의 호환성을 위한 getter 메서드
     */
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

