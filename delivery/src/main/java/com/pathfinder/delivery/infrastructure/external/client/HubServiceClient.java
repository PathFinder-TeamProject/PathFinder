package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.fallback.HubServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
    name = "hub-service", 
    path = "/api/v1",
    fallback = HubServiceFallback.class
)
public interface HubServiceClient {
    
    /**
     * 허브 조회
     * 실제 컨트롤러는 ResponseEntity<HubResponseDto>를 직접 반환 (래퍼 없음)
     * Feign이 자동으로 ResponseEntity의 body를 추출
     */
    @GetMapping("/hubs/{hubId}")
    HubDto getHub(@PathVariable("hubId") UUID hubId);
    
    /**
     * 모든 허브 경로 조회
     * 실제 컨트롤러는 ResponseEntity<List<HubRoute>>를 직접 반환 (래퍼 없음)
     * Feign이 자동으로 ResponseEntity의 body를 추출
     */
    @GetMapping("/hub-routes")
    List<HubRouteDto> getAllHubRoutes();
    
    /**
     * 경로 찾기
     * 실제 컨트롤러는 ResponseEntity<List<HubRoute>>를 직접 반환 (래퍼 없음)
     * Feign이 자동으로 ResponseEntity의 body를 추출
     */
    @GetMapping("/hub-routes/path")
    List<HubRouteDto> findPath(
        @RequestParam("origin") UUID origin,
        @RequestParam("destination") UUID destination
    );
}
