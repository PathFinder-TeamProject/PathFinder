package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.fallback.DeliveryManagerServiceFallback;
import com.pathfinder.global.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "delivery-manager-service",
        path = "/internal",
        fallback = DeliveryManagerServiceFallback.class
)
public interface DeliveryManagerServiceClient {
    
    @GetMapping("/deliverys/{deliveryManagerId}")
    ApiResponse<DeliveryManagerDto> getDeliveryManager(@PathVariable("deliveryManagerId") UUID deliveryManagerId);
    
    @GetMapping("/deliverys/hub/{hubId}")
    ApiResponse<List<DeliveryManagerDto>> getDeliveryManagersByHub(@PathVariable("hubId") UUID hubId);
    
    @GetMapping("/deliverys/hub/{hubId}/type/{type}")
    ApiResponse<List<DeliveryManagerDto>> getDeliveryManagersByHubAndType(
        @PathVariable("hubId") UUID hubId,
        @PathVariable("type") String type
    );
}
