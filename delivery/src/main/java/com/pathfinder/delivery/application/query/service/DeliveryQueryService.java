package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DeliveryQueryService {
    
    DeliveryDto findById(UUID id);
    
    DeliveryDto findByOrderId(UUID orderId);
    
    Page<DeliveryDto> searchDeliveries(
        UUID hubId,
        DeliveryStatus status,
        UUID deliveryManagerId,
        Pageable pageable
    );
    
    List<DeliveryDto> findAll();
}
