package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.command.service.impl.DeliveryRouteCommandServiceImpl;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.global.presentation.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryRouteCommandServiceTest {

    @Mock
    private DeliveryRouteRepository routeRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryManagerServiceClient deliveryManagerServiceClient;

    @Mock
    private MessageServiceClient messageServiceClient;

    @InjectMocks
    private DeliveryRouteCommandServiceImpl routeCommandService;

    private UUID deliveryId;
    private UUID routeId;
    private UUID fromHubId;
    private UUID toHubId;

    @BeforeEach
    void setUp() {
        deliveryId = UUID.randomUUID();
        routeId = UUID.randomUUID();
        fromHubId = UUID.randomUUID();
        toHubId = UUID.randomUUID();
    }

    @Test
    @DisplayName("경로 상태가 PICKED_UP으로 변경될 때 출발 허브 담당자에서 도착 허브 담당자에게 발송 시작 알림을 보낸다")
    void updateRoute_shouldSendNotificationFromFromHubToToHub_whenStatusChangedToPickedUp() {
        // given
        DeliveryRouteEntity existingRoute = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .sequence(0)
                .status(DeliveryRouteStatus.READY)
                .build();

        CreateDeliveryRouteCommandDto command = CreateDeliveryRouteCommandDto.builder()
                .deliveryId(deliveryId)
                .status(DeliveryRouteStatus.PICKED_UP)
                .build();

        DeliveryManagerDto fromHubManager = DeliveryManagerDto.builder()
                .deliveryManagerId(1L)
                .username("hub_manager_a")
                .slackId("slack_id_a")
                .type("HUB")
                .hubId(fromHubId)
                .build();

        DeliveryManagerDto toHubManager = DeliveryManagerDto.builder()
                .deliveryManagerId(2L)
                .username("hub_manager_b")
                .slackId("slack_id_b")
                .type("HUB")
                .hubId(toHubId)
                .build();

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));
        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(fromHubId, "HUB"))
                .thenReturn(List.of(fromHubManager));
        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(toHubId, "HUB"))
                .thenReturn(List.of(toHubManager));

        // when
        routeCommandService.updateRoute(routeId, command);

        // then
        verify(messageServiceClient, times(1)).sendSlackMessage(argThat(request -> {
            return request.getSenderId().equals("hub_manager_a") && 
                   request.getReceiverId().equals("hub_manager_b");
        }));
    }

    @Test
    @DisplayName("경로 상태가 DELIVERED로 변경될 때 출발 허브 담당자에서 도착 허브 담당자에게 도착 알림을 보낸다")
    void updateRoute_shouldSendNotificationFromFromHubToToHub_whenStatusChangedToDelivered() {
        // given
        DeliveryRouteEntity existingRoute = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .sequence(0)
                .status(DeliveryRouteStatus.IN_TRANSIT)
                .build();

        CreateDeliveryRouteCommandDto command = CreateDeliveryRouteCommandDto.builder()
                .deliveryId(deliveryId)
                .status(DeliveryRouteStatus.DELIVERED)
                .build();

        DeliveryManagerDto fromHubManager = DeliveryManagerDto.builder()
                .deliveryManagerId(1L)
                .username("hub_manager_a")
                .slackId("slack_id_a")
                .type("HUB")
                .hubId(fromHubId)
                .build();

        DeliveryManagerDto toHubManager = DeliveryManagerDto.builder()
                .deliveryManagerId(2L)
                .username("hub_manager_b")
                .slackId("slack_id_b")
                .type("HUB")
                .hubId(toHubId)
                .build();

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));
        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(fromHubId, "HUB"))
                .thenReturn(List.of(fromHubManager));
        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(toHubId, "HUB"))
                .thenReturn(List.of(toHubManager));

        // when
        routeCommandService.updateRoute(routeId, command);

        // then
        verify(messageServiceClient, times(1)).sendSlackMessage(argThat(request -> {
            return request.getSenderId().equals("hub_manager_a") && 
                   request.getReceiverId().equals("hub_manager_b");
        }));
    }
}

