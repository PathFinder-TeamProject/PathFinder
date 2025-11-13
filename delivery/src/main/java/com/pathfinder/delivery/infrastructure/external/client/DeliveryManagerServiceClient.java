package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.fallback.DeliveryManagerServiceFallback;
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
    
    @GetMapping("/{deliveryManagerId}")
    DeliveryManagerDto getDeliveryManager(@PathVariable("deliveryManagerId") Long deliveryManagerId);
    
    @GetMapping("/hub/{hubId}")
    List<DeliveryManagerDto> getDeliveryManagersByHub(@PathVariable("hubId") UUID hubId);
    
    @GetMapping("/hub/{hubId}/type/{type}")
    List<DeliveryManagerDto> getDeliveryManagersByHubAndType(
        @PathVariable("hubId") UUID hubId,
        @PathVariable("type") String type
    );
}
