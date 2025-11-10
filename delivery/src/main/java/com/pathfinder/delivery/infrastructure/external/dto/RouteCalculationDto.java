package com.pathfinder.delivery.infrastructure.external.dto;

import com.pathfinder.delivery.application.dto.response.RouteCalculationResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCalculationDto {
    private List<UUID> path;
    private Double totalDistance;
    private Integer totalTime;
    
    public RouteCalculationResultDto toResultDto() {
        return RouteCalculationResultDto.builder()
            .path(this.path)
            .totalDistance(this.totalDistance)
            .totalTime(this.totalTime)
            .build();
    }
}

