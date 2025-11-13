package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteCalculationServiceTest {

    @Mock
    private HubServiceClient hubServiceClient;

    private RouteCalculationService routeCalculationService;

    @BeforeEach
    void setUp() {
        routeCalculationService = new RouteCalculationService(hubServiceClient);
    }

    @Test
    @DisplayName("최단 경로 계산 시 경로가 있으면 경로와 거리를 반환한다")
    void calculateShortestPath_shouldReturnPathAndDistance_whenRoutesExist() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(hubA)
                .destinationHubId(hubB)
                .distanceKm(100.0)
                .durationMin(60)
                .build();

        HubRouteDto route2 = HubRouteDto.builder()
                .originHubId(hubB)
                .destinationHubId(hubC)
                .distanceKm(150.0)
                .durationMin(90)
                .build();

        when(hubServiceClient.findPath(hubA, hubC)).thenReturn(List.of(route1, route2));

        // when
        RouteCalculationDto result = routeCalculationService.calculateShortestPath(hubA, hubC);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPath()).containsExactly(hubA, hubB, hubC);
        assertThat(result.getTotalDistance()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("최단 경로 계산 시 경로가 없으면 출발지와 도착지만 포함한 경로를 반환한다")
    void calculateShortestPath_shouldReturnStartAndEndOnly_whenNoRoutesExist() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();

        when(hubServiceClient.findPath(hubA, hubB)).thenReturn(null);

        // when
        RouteCalculationDto result = routeCalculationService.calculateShortestPath(hubA, hubB);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPath()).containsExactly(hubA, hubB);
        assertThat(result.getTotalDistance()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("최단 경로 계산 시 빈 경로 리스트면 출발지와 도착지만 포함한 경로를 반환한다")
    void calculateShortestPath_shouldReturnStartAndEndOnly_whenEmptyRoutes() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();

        when(hubServiceClient.findPath(hubA, hubB)).thenReturn(new ArrayList<>());

        // when
        RouteCalculationDto result = routeCalculationService.calculateShortestPath(hubA, hubB);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPath()).containsExactly(hubA, hubB);
        assertThat(result.getTotalDistance()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("최단 경로 계산 시 중복된 도착지가 있으면 중복을 제거한다")
    void calculateShortestPath_shouldRemoveDuplicateArrivals() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(hubA)
                .destinationHubId(hubB)
                .distanceKm(100.0)
                .build();

        HubRouteDto route2 = HubRouteDto.builder()
                .originHubId(hubB)
                .destinationHubId(hubC)
                .distanceKm(150.0)
                .build();

        HubRouteDto route3 = HubRouteDto.builder()
                .originHubId(hubC)
                .destinationHubId(hubB)  // 중복
                .distanceKm(50.0)
                .build();

        when(hubServiceClient.findPath(hubA, hubC)).thenReturn(List.of(route1, route2, route3));

        // when
        RouteCalculationDto result = routeCalculationService.calculateShortestPath(hubA, hubC);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPath()).contains(hubA, hubB, hubC);
        assertThat(result.getTotalDistance()).isEqualTo(300.0);
    }

    @Test
    @DisplayName("최단 경로 계산 시 거리가 null인 경로는 거리 합계에 포함하지 않는다")
    void calculateShortestPath_shouldIgnoreNullDistance() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(hubA)
                .destinationHubId(hubB)
                .distanceKm(100.0)
                .build();

        HubRouteDto route2 = HubRouteDto.builder()
                .originHubId(hubB)
                .destinationHubId(hubC)
                .distanceKm(null)  // null 거리
                .build();

        when(hubServiceClient.findPath(hubA, hubC)).thenReturn(List.of(route1, route2));

        // when
        RouteCalculationDto result = routeCalculationService.calculateShortestPath(hubA, hubC);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalDistance()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("모든 경로 조회 시 허브 서비스에서 모든 경로를 반환한다")
    void getAllRoutes_shouldReturnAllRoutes() {
        // given
        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(UUID.randomUUID())
                .destinationHubId(UUID.randomUUID())
                .distanceKm(100.0)
                .build();

        HubRouteDto route2 = HubRouteDto.builder()
                .originHubId(UUID.randomUUID())
                .destinationHubId(UUID.randomUUID())
                .distanceKm(200.0)
                .build();

        when(hubServiceClient.getAllHubRoutes()).thenReturn(List.of(route1, route2));

        // when
        List<HubRouteDto> result = routeCalculationService.getAllRoutes();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(route1, route2);
    }

    @Test
    @DisplayName("단일 경로 조회 시 출발지와 도착지가 일치하는 경로를 반환한다")
    void getRoute_shouldReturnMatchingRoute() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(hubA)
                .destinationHubId(hubB)
                .distanceKm(100.0)
                .build();

        HubRouteDto route2 = HubRouteDto.builder()
                .originHubId(hubB)
                .destinationHubId(hubC)
                .distanceKm(150.0)
                .build();

        when(hubServiceClient.findPath(hubA, hubB)).thenReturn(List.of(route1, route2));

        // when
        HubRouteDto result = routeCalculationService.getRoute(hubA, hubB);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDepart()).isEqualTo(hubA);
        assertThat(result.getArrive()).isEqualTo(hubB);
        assertThat(result.getDistance()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("단일 경로 조회 시 일치하는 경로가 없으면 첫 번째 경로를 반환한다")
    void getRoute_shouldReturnFirstRoute_whenNoExactMatch() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        HubRouteDto route1 = HubRouteDto.builder()
                .originHubId(hubA)
                .destinationHubId(hubB)
                .distanceKm(100.0)
                .build();

        when(hubServiceClient.findPath(hubA, hubC)).thenReturn(List.of(route1));

        // when
        HubRouteDto result = routeCalculationService.getRoute(hubA, hubC);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(route1);
    }

    @Test
    @DisplayName("단일 경로 조회 시 경로가 없으면 null을 반환한다")
    void getRoute_shouldReturnNull_whenNoRoutes() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();

        when(hubServiceClient.findPath(hubA, hubB)).thenReturn(null);

        // when
        HubRouteDto result = routeCalculationService.getRoute(hubA, hubB);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("단일 경로 조회 시 빈 경로 리스트면 null을 반환한다")
    void getRoute_shouldReturnNull_whenEmptyRoutes() {
        // given
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();

        when(hubServiceClient.findPath(hubA, hubB)).thenReturn(new ArrayList<>());

        // when
        HubRouteDto result = routeCalculationService.getRoute(hubA, hubB);

        // then
        assertThat(result).isNull();
    }
}

