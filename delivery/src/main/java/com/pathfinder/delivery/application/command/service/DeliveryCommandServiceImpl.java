package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommand;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCommandServiceImpl implements DeliveryCommandService {

    @Override
    public DeliveryDto createDelivery(CreateDeliveryCommand command) {
        return null;
    }

    @Override
    public DeliveryDto updateDelivery(UpdateDeliveryCommand command) {
        return null;
    }

    @Override
    public void deleteDelivery(Long id) {
    }
}

