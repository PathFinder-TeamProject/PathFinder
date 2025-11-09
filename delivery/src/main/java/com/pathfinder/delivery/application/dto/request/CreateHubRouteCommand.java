package com.pathfinder.delivery.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHubRouteCommand {
    @NotNull
    private UUID depart;
    
    @NotNull
    private UUID arrive;
    
    private Integer time;
    private Double distance;
}

