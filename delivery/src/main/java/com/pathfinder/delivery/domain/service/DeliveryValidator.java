package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.ApiResponseDto;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryValidator {

    private final OrderServiceClient orderServiceClient;
    private final HubServiceClient hubServiceClient;
    private final DeliveryManagerServiceClient deliveryManagerServiceClient;

    public OrderDto validateAndGetOrder(UUID orderId) {
        ResponseEntity<ApiResponseDto<OrderDto>> response = orderServiceClient.getOrder(orderId);
        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new PathException(DeliveryErrorCode.ORDER_NOT_FOUND);
        }
        return response.getBody().getData();
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
        ApiResponse<DeliveryManagerDto> response = deliveryManagerServiceClient.getDeliveryManager(deliveryManagerId);
        if (response == null || response.getData() == null) {
            throw new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }
        return response.getData();
    }
}

