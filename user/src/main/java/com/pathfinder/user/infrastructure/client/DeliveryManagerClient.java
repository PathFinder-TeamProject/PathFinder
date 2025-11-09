package com.pathfinder.user.infrastructure.client;

import com.pathfinder.user.application.dto.request.DeliveryManagerRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-manager-service", path = "/v1/delivery-managers")
public interface DeliveryManagerClient {

    @PostMapping
    void createDeliveryManager(@RequestBody DeliveryManagerRequestDto request);
}