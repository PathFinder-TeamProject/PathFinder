package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryValidator {

    private final OrderServiceClient orderServiceClient;
    private final HubServiceClient hubServiceClient;
    private final DeliveryManagerServiceClient deliveryManagerServiceClient;

    public OrderDto validateAndGetOrder(UUID orderId) {
        OrderDto order = orderServiceClient.getOrder(orderId);
        if (order == null) {
            throw new PathException(DeliveryErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    public HubDto validateAndGetHub(UUID hubId) {
        if (hubId == null) {
            return null;
        }
        HubDto hub = hubServiceClient.getHub(hubId);
        if (hub == null) {
            throw new PathException(DeliveryErrorCode.HUB_NOT_FOUND);
        }
        return hub;
    }

    public DeliveryManagerDto validateAndGetDeliveryManager(UUID deliveryManagerId) {
        DeliveryManagerDto manager = deliveryManagerServiceClient.getDeliveryManager(deliveryManagerId);
        if (manager == null) {
            throw new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }
        return manager;
    }
}

