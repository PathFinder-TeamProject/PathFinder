package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryRouteFactoryTest {

    @Mock
    private HubServiceClient hubServiceClient;

    @Mock
    private RouteCalculationService routeCalculationService;

    @Mock
    private DeliveryManagerAssignmentService deliveryManagerAssignmentService;

    private DeliveryRouteFactory routeFactory;

    @BeforeEach
    void setUp() {
        routeFactory = new DeliveryRouteFactory(
                routeCalculationService,
                deliveryManagerAssignmentService
        );
    }

    @Test
    @DisplayName("여러 경로 생성 시 각 경로마다 해당 fromHubId의 HUB 타입 담당자가 배정된다")
    void createRoutes_shouldAssignHubManagerForEachRoute_whenMultipleRoutes() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();
        List<UUID> routePath = List.of(hubA, hubB, hubC);

        UUID hubManagerA = UUID.randomUUID();
        UUID hubManagerB = UUID.randomUUID();

        HubRouteDto routeAB = HubRouteDto.builder()
                .durationMin(60)
                .distanceKm(100.0)
                .originHubId(hubA)
                .destinationHubId(hubB)
                .build();
        HubRouteDto routeBC = HubRouteDto.builder()
                .durationMin(90)
                .distanceKm(150.0)
                .originHubId(hubB)
                .destinationHubId(hubC)
                .build();

        when(routeCalculationService.getRoute(hubA, hubB))
                .thenReturn(routeAB);
        when(routeCalculationService.getRoute(hubB, hubC))
                .thenReturn(routeBC);
        when(deliveryManagerAssignmentService.assignDeliveryManager(hubA, "HUB"))
                .thenReturn(hubManagerA);
        when(deliveryManagerAssignmentService.assignDeliveryManager(hubB, "HUB"))
                .thenReturn(hubManagerB);

        // when
        List<DeliveryRouteEntity> routes = routeFactory.createRoutes(
                deliveryId,
                routePath,
                UUID.randomUUID()      
        );

        // then
        assertThat(routes).hasSize(2);

        // A → B 경로
        DeliveryRouteEntity route1 = routes.get(0);
        assertThat(route1.getFromHubId()).isEqualTo(hubA);
        assertThat(route1.getToHubId()).isEqualTo(hubB);
        assertThat(route1.getSequence()).isEqualTo(0);
        assertThat(route1.getDeliveryManagerId()).isEqualTo(hubManagerA);

        // B → C 경로
        DeliveryRouteEntity route2 = routes.get(1);
        assertThat(route2.getFromHubId()).isEqualTo(hubB);
        assertThat(route2.getToHubId()).isEqualTo(hubC);
        assertThat(route2.getSequence()).isEqualTo(1);
        assertThat(route2.getDeliveryManagerId()).isEqualTo(hubManagerB);

        verify(deliveryManagerAssignmentService).assignDeliveryManager(hubA, "HUB");
        verify(deliveryManagerAssignmentService).assignDeliveryManager(hubB, "HUB");
        verify(deliveryManagerAssignmentService, never()).assignDeliveryManager(eq(hubC), anyString());
    }

    @Test
    @DisplayName("초기 경로 생성 시 fromHubId의 HUB 타입 담당자가 배정된다")
    void createInitialRoute_shouldAssignHubManagerFromFromHubId() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        BigDecimal expectedDistance = BigDecimal.valueOf(100.5);
        UUID hubManagerId = UUID.randomUUID();

        when(deliveryManagerAssignmentService.assignDeliveryManager(fromHubId, "HUB"))
                .thenReturn(hubManagerId);

        // when
        DeliveryRouteEntity route = routeFactory.createInitialRoute(
                deliveryId,
                fromHubId,
                toHubId,
                expectedDistance,
                UUID.randomUUID()
        );

        // then
        assertThat(route.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(route.getFromHubId()).isEqualTo(fromHubId);
        assertThat(route.getToHubId()).isEqualTo(toHubId);
        assertThat(route.getDeliveryManagerId()).isEqualTo(hubManagerId);
        assertThat(route.getExpectedDistance()).isEqualTo(expectedDistance);

        verify(deliveryManagerAssignmentService).assignDeliveryManager(fromHubId, "HUB");
    }
}

