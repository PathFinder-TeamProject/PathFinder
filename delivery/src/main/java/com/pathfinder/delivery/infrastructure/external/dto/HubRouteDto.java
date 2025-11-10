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
    private Integer time;
    private Double distance;
    private UUID depart;
    private UUID arrive;
}

