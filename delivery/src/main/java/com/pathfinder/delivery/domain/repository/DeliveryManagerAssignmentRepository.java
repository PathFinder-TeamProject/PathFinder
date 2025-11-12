package com.pathfinder.delivery.domain.repository;

import java.util.UUID;

public interface DeliveryManagerAssignmentRepository {

    Integer getLastAssignedOrder(UUID hubId);

    void saveLastAssignedOrder(UUID hubId, Integer order);
}

