package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;

import java.util.UUID;

public interface DeliveryCommandService {
    
    DeliveryDto createDelivery(CreateDeliveryCommandDto command);
    
    DeliveryDto updateDelivery(UpdateDeliveryCommandDto command);
    
    void deleteDelivery(UUID id);
}
