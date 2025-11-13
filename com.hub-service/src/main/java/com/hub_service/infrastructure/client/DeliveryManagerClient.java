package com.hub_service.infrastructure.client;

import com.hub_service.infrastructure.config.FeignClientConfig;
import com.hub_service.presentation.dto.response.HubResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "delivery-manager-service",
        path = "/internal",
        configuration = FeignClientConfig.class)
public interface DeliveryManagerClient {
    @DeleteMapping("/hubs/{hubId}")
    ResponseEntity<Void> notifyHubDeletion(@PathVariable("hubId") String hubId);
}