package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommand;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;

import java.util.UUID;

public interface DeliveryCommandService {
    
    DeliveryDto createDelivery(CreateDeliveryCommand command);
    
    DeliveryDto updateDelivery(UpdateDeliveryCommand command);
    
    void deleteDelivery(UUID id);
}
