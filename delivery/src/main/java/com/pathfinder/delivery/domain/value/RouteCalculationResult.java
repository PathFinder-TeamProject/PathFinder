package com.pathfinder.delivery.domain.value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class RouteCalculationResult {
    private final List<UUID> path;
    private final BigDecimal totalExpectedDistance;

    public RouteCalculationResult(List<UUID> path, BigDecimal totalExpectedDistance) {
        this.path = path;
        this.totalExpectedDistance = totalExpectedDistance;
    }

    public List<UUID> getPath() {
        return path;
    }

    public BigDecimal getTotalExpectedDistance() {
        return totalExpectedDistance;
    }

    public boolean hasValidPath() {
        return path != null && path.size() > 1;
    }
}

