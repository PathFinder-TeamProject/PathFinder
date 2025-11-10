package com.pathfinder.delivery.infrastructure.config;

import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 로컬 환경용 Mock FeignClient 설정
 * 모든 외부 서비스 호출을 Mock 데이터로 대체
 */
@Slf4j
@Configuration
@Profile("local")
public class LocalMockConfig {

    @Bean
    @Primary
    public OrderServiceClient mockOrderServiceClient() {
        return new OrderServiceClient() {
            @Override
            public OrderDto getOrder(UUID orderId) {
                log.info("[MOCK] OrderServiceClient.getOrder called with orderId: {}", orderId);
                return OrderDto.builder()
                        .orderId(orderId)
                        .productId(UUID.randomUUID())
                        .supplierCompanyId(UUID.randomUUID())
                        .receiverCompanyId(UUID.randomUUID())
                        .status("PENDING")
                        .quantity(10L)
                        .request("테스트 주문")
                        .deadline(LocalDateTime.now().plusDays(3))
                        .build();
            }
        };
    }

    @Bean
    @Primary
    public HubServiceClient mockHubServiceClient() {
        return new HubServiceClient() {
            @Override
            public HubDto getHub(UUID hubId) {
                log.info("[MOCK] HubServiceClient.getHub called with hubId: {}", hubId);
                return HubDto.builder()
                        .hubId(hubId)
                        .hubName("테스트 허브 " + hubId.toString().substring(0, 8))
                        .hubAddress("서울시 강남구 테스트로 123")
                        .lat(37.5665)
                        .lng(126.9780)
                        .build();
            }

            @Override
            public List<HubRouteDto> getAllHubRoutes() {
                log.info("[MOCK] HubServiceClient.getAllHubRoutes called");
                return new ArrayList<>();
            }

            @Override
            public HubRouteDto getHubRoute(UUID routeId) {
                log.info("[MOCK] HubServiceClient.getHubRoute called with routeId: {}", routeId);
                return HubRouteDto.builder()
                        .routeId(routeId)
                        .depart(UUID.randomUUID())
                        .arrive(UUID.randomUUID())
                        .distance(100.5)
                        .time(60)
                        .build();
            }

            @Override
            public List<HubRouteDto> getRoutesByDepart(UUID departHubId) {
                log.info("[MOCK] HubServiceClient.getRoutesByDepart called with departHubId: {}", departHubId);
                return new ArrayList<>();
            }

            @Override
            public List<HubRouteDto> getRoutesByArrive(UUID arriveHubId) {
                log.info("[MOCK] HubServiceClient.getRoutesByArrive called with arriveHubId: {}", arriveHubId);
                return new ArrayList<>();
            }

            @Override
            public HubRouteDto findRouteByDepartAndArrive(UUID depart, UUID arrive) {
                log.info("[MOCK] HubServiceClient.findRouteByDepartAndArrive called with depart: {}, arrive: {}", depart, arrive);
                return HubRouteDto.builder()
                        .routeId(UUID.randomUUID())
                        .depart(depart)
                        .arrive(arrive)
                        .distance(100.5)
                        .time(60)
                        .build();
            }

            @Override
            public RouteCalculationDto calculateRoute(UUID start, UUID end) {
                log.info("[MOCK] HubServiceClient.calculateRoute called with start: {}, end: {}", start, end);
                
                // Mock 경로 생성 (시작 -> 중간 -> 끝)
                List<UUID> path = new ArrayList<>();
                path.add(start);
                
                // 중간 허브 추가 (선택적)
                if (!start.equals(end)) {
                    path.add(UUID.randomUUID()); // 중간 허브
                }
                
                path.add(end);
                
                return RouteCalculationDto.builder()
                        .path(path)
                        .totalDistance(150.5)
                        .totalTime(90)
                        .build();
            }
        };
    }

    @Bean
    @Primary
    public DeliveryManagerServiceClient mockDeliveryManagerServiceClient() {
        return new DeliveryManagerServiceClient() {
            @Override
            public DeliveryManagerDto getDeliveryManager(UUID deliveryManagerId) {
                log.info("[MOCK] DeliveryManagerServiceClient.getDeliveryManager called with deliveryManagerId: {}", deliveryManagerId);
                return DeliveryManagerDto.builder()
                        .deliveryManagerId(deliveryManagerId)
                        .userId(UUID.randomUUID())
                        .type("HUB_DELIVERY")
                        .deliveryOrder(1)
                        .hubId(UUID.randomUUID())
                        .build();
            }

            @Override
            public List<DeliveryManagerDto> getDeliveryManagersByHub(UUID hubId) {
                log.info("[MOCK] DeliveryManagerServiceClient.getDeliveryManagersByHub called with hubId: {}", hubId);
                List<DeliveryManagerDto> managers = new ArrayList<>();
                managers.add(DeliveryManagerDto.builder()
                        .deliveryManagerId(UUID.randomUUID())
                        .userId(UUID.randomUUID())
                        .type("HUB_DELIVERY")
                        .deliveryOrder(1)
                        .hubId(hubId)
                        .build());
                return managers;
            }

            @Override
            public List<DeliveryManagerDto> getDeliveryManagersByHubAndType(UUID hubId, String type) {
                log.info("[MOCK] DeliveryManagerServiceClient.getDeliveryManagersByHubAndType called with hubId: {}, type: {}", hubId, type);
                List<DeliveryManagerDto> managers = new ArrayList<>();
                managers.add(DeliveryManagerDto.builder()
                        .deliveryManagerId(UUID.randomUUID())
                        .userId(UUID.randomUUID())
                        .type(type)
                        .deliveryOrder(1)
                        .hubId(hubId)
                        .build());
                return managers;
            }
        };
    }
}

