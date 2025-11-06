package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryQueryServiceImpl implements DeliveryQueryService {

    @Override
    public DeliveryDto getDelivery(Long id) {
        return null;
    }

    @Override
    public List<DeliveryDto> getAllDeliveries() {
        return null;
    }

    @Override
    public DeliveryDto getDeliveryByOrderId(Long orderId) {
        return null;
    }
}

