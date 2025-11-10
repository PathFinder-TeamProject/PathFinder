package com.pathfinder.delivery.application.dto.response;

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
public class RouteCalculationResultDto {
    private List<UUID> path;
    private Double totalDistance;
    private Integer totalTime;
}

