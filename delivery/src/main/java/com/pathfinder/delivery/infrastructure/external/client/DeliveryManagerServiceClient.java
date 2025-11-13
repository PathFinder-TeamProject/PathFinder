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
    
    /**
     * 배송담당자 조회
     * 실제 컨트롤러는 ApiResponse<DeliveryManagerResponseDto>를 반환
     * Jackson이 DeliveryManagerResponseDto를 DeliveryManagerDto로 자동 변환 (필드명 일치)
     */
    @GetMapping("/deliverys/{deliveryManagerId}")
    ApiResponse<DeliveryManagerDto> getDeliveryManager(@PathVariable("deliveryManagerId") UUID deliveryManagerId);
    
    /**
     * 허브별 배송담당자 목록 조회
     * 실제 컨트롤러는 ApiResponse<List<DeliveryManagerResponseDto>>를 반환
     */
    @GetMapping("/deliverys/hub/{hubId}")
    ApiResponse<List<DeliveryManagerDto>> getDeliveryManagersByHub(@PathVariable("hubId") UUID hubId);
    
    /**
     * 허브 및 타입별 배송담당자 목록 조회
     * 실제 컨트롤러는 ApiResponse<List<DeliveryManagerResponseDto>>를 반환
     */
    @GetMapping("/deliverys/hub/{hubId}/type/{type}")
    ApiResponse<List<DeliveryManagerDto>> getDeliveryManagersByHubAndType(
        @PathVariable("hubId") UUID hubId,
        @PathVariable("type") String type
    );
}
