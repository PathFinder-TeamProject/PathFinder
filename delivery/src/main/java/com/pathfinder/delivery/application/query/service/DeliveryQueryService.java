package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;

import java.util.List;

public interface DeliveryQueryService {
    
    DeliveryDto getDelivery(Long id);
    
    List<DeliveryDto> getAllDeliveries();
    
    DeliveryDto getDeliveryByOrderId(Long orderId);
}

