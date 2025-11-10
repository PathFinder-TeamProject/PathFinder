package com.pathfinder.delivery.domain.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeliveryQueryRepository {
    
    Page<DeliveryEntity> searchDeliveries(
        UUID hubId,
        DeliveryStatus status,
        UUID deliveryManagerId,
        Pageable pageable
    );
}

