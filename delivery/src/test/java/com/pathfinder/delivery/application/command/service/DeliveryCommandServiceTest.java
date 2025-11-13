package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.domain.service.DeliveryRouteFactory;
import com.pathfinder.delivery.domain.service.DeliveryStatusValidator;
import com.pathfinder.delivery.domain.service.DeliveryValidator;
import com.pathfinder.delivery.application.outbox.DeliveryOutboxService;
import com.pathfinder.delivery.application.command.service.impl.DeliveryCommandServiceImpl;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.delivery.domain.value.RouteCalculationResult;
import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.domain.service.DeliveryManagerAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.domain.service.RouteCalculationService;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class DeliveryCommandServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryRouteRepository routeRepository;

    @Mock
    private DeliveryValidator deliveryValidator;

    @Mock
    private DeliveryRouteFactory routeFactory;

    @Mock
    private HubServiceClient hubServiceClient;

    @Mock
    private RouteCalculationService routeCalculationService;

    private DeliveryRouteFactory realRouteFactory;

    @Mock
    private DeliveryOutboxService deliveryOutboxService;

    @Mock
    private DeliveryStatusValidator statusValidator;

    @Mock
    private MessageServiceClient messageServiceClient;

    @Mock
    private DeliveryManagerAssignmentService deliveryManagerAssignmentService;

    @Mock
    private DeliveryManagerServiceClient deliveryManagerServiceClient;

    private DeliveryCommandService deliveryCommandService;

    @BeforeEach
    void setUp() {
        realRouteFactory = new DeliveryRouteFactory(
                hubServiceClient,
                routeCalculationService,
                deliveryManagerAssignmentService
        );
        deliveryCommandService = new DeliveryCommandServiceImpl(
                deliveryRepository,
                routeRepository,
                deliveryValidator,
                routeFactory,
                deliveryOutboxService,
                statusValidator,
                messageServiceClient,
                deliveryManagerAssignmentService,
                deliveryManagerServiceClient
        );
    }

    @Test
    @DisplayName("배송 생성 시 유효한 명령으로 배송을 생성하고 저장한다")
    void createDelivery_shouldCreateAndSaveDelivery_whenValidCommand() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        Long receiverId = 12345L;
        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(deliveryManagerId)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .receiverSlackId(receiverId.toString())
                .build();

        DeliveryEntity savedEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .receiverSlackId("hong@example.com")
                .build();

        DeliveryManagerDto managerDto = 
            DeliveryManagerDto.builder()
                .deliveryManagerId(deliveryManagerId)
                .username("testuser")
                .type("COMPANY")
                .deliveryOrder(0)
                .hubId(toHubId)
                .build();
        
        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(fromHubId)).thenReturn(null);
        when(deliveryValidator.validateAndGetDeliveryManager(deliveryManagerId)).thenReturn(managerDto);
        when(routeFactory.calculateRoute(fromHubId, toHubId, BigDecimal.valueOf(100.5)))
                .thenReturn(new RouteCalculationResult(null, BigDecimal.valueOf(100.5)));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(savedEntity);

        // when
        DeliveryDto result = deliveryCommandService.createDelivery(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getDeliveryManagerId()).isEqualTo(deliveryManagerId);
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.READY);

        verify(deliveryValidator).validateAndGetOrder(orderId);
        verify(deliveryValidator).validateAndGetHub(fromHubId);
        verify(deliveryValidator).validateAndGetDeliveryManager(deliveryManagerId);
        verify(routeFactory).calculateRoute(fromHubId, toHubId, BigDecimal.valueOf(100.5));
        verify(deliveryRepository).save(any(DeliveryEntity.class));
        verify(deliveryOutboxService).enqueue(any(DeliveryEventDto.class));
    }

    @Test
    @DisplayName("배송 생성 시 저장 중 예외가 발생하면 예외를 전파한다")
    void createDelivery_shouldThrowException_whenSaveFails() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();
        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .build();

        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(any())).thenReturn(null);
        when(deliveryValidator.validateAndGetDeliveryManager(deliveryManagerId)).thenReturn(null);
        when(routeFactory.calculateRoute(any(), any(), any()))
                .thenReturn(new RouteCalculationResult(null, BigDecimal.valueOf(100.5)));
        when(deliveryRepository.save(any(DeliveryEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> deliveryCommandService.createDelivery(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(deliveryRepository).save(any(DeliveryEntity.class));
        verify(deliveryManagerAssignmentService, never()).assignDeliveryManager(any());
    }

    @Test
    @DisplayName("배송 업데이트 시 유효한 명령으로 배송을 수정한다")
    void updateDelivery_shouldUpdateDelivery_whenValidCommand() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        UpdateDeliveryCommandDto command = UpdateDeliveryCommandDto.builder()
                .deliveryId(deliveryId)
                .status("IN_TRANSIT")
                .actualDistance(BigDecimal.valueOf(95.0))
                .receiverName("김철수")
                .build();

        DeliveryEntity existingEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.0))
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(java.util.Optional.of(existingEntity));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(existingEntity);

        // when
        DeliveryDto result = deliveryCommandService.updateDelivery(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(result.getActualDistance()).isEqualTo(BigDecimal.valueOf(95.0));
        assertThat(result.getReceiverName()).isEqualTo("김철수");

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(any(DeliveryEntity.class));
        verify(deliveryOutboxService).enqueue(any(DeliveryEventDto.class));
    }

    @Test
    @DisplayName("존재하지 않는 배송 업데이트 시 예외를 발생시킨다")
    void updateDelivery_shouldThrowException_whenDeliveryNotFound() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UpdateDeliveryCommandDto command = UpdateDeliveryCommandDto.builder()
                .deliveryId(deliveryId)
                .status("IN_TRANSIT")
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryCommandService.updateDelivery(command))
                .isInstanceOf(RuntimeException.class);

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any(DeliveryEntity.class));
    }

    @Test
    @DisplayName("배송 삭제 시 배송을 취소 상태로 변경한다")
    void deleteDelivery_shouldCancelDelivery() {
        // given
        UUID deliveryId = UUID.randomUUID();
        DeliveryEntity deliveryEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(UUID.randomUUID())
                .deliveryManagerId(UUID.randomUUID())
                .status(DeliveryStatus.READY)
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(java.util.Optional.of(deliveryEntity));

        // when
        deliveryCommandService.deleteDelivery(deliveryId);

        // then
        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).softDelete(eq(deliveryId), anyString());
        assertThat(deliveryEntity.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
    }

    @Test
    @DisplayName("존재하지 않는 배송 삭제 시 예외를 발생시킨다")
    void deleteDelivery_shouldThrowException_whenDeliveryNotFound() {
        // given
        UUID deliveryId = UUID.randomUUID();

        when(deliveryRepository.findById(deliveryId)).thenReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryCommandService.deleteDelivery(deliveryId))
                .isInstanceOf(RuntimeException.class);

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any(DeliveryEntity.class));
    }

    @Test
    @DisplayName("배송 생성 시 deliveryManagerId가 null이면 자동으로 배송담당자를 배정한다")
    void createDelivery_shouldAssignDeliveryManagerAutomatically_whenDeliveryManagerIdIsNull() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        UUID assignedManagerId = UUID.randomUUID();
        Long receiverId = 12345L;

        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(null) // null이면 자동 배정
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .receiverSlackId(receiverId.toString())
                .build();

        DeliveryEntity savedEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(assignedManagerId)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .receiverSlackId(receiverId.toString())
                .build();

        DeliveryManagerDto managerDto = 
            DeliveryManagerDto.builder()
                .deliveryManagerId(assignedManagerId)
                .username("testuser")
                .type("COMPANY")
                .deliveryOrder(0)
                .hubId(toHubId)
                .build();
        
        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(fromHubId)).thenReturn(null);
        when(deliveryManagerAssignmentService.assignDeliveryManager(toHubId))
                .thenReturn(assignedManagerId);
        when(routeFactory.calculateRoute(fromHubId, toHubId, BigDecimal.valueOf(100.5)))
                .thenReturn(new RouteCalculationResult(null, BigDecimal.valueOf(100.5)));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(savedEntity);

        // when
        DeliveryDto result = deliveryCommandService.createDelivery(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryManagerId()).isEqualTo(assignedManagerId);
        verify(deliveryManagerAssignmentService).assignDeliveryManager(toHubId);
        verify(deliveryRepository).save(any(DeliveryEntity.class));
    }

    @Test
    @DisplayName("배송 생성 시 deliveryOrder가 낮은 담당자를 우선 배정한다")
    void createDelivery_shouldAssignManagerWithLowestDeliveryOrder_whenMultipleManagersExist() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        UUID managerWithOrder0 = UUID.randomUUID();

        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(null)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .build();

        DeliveryEntity savedEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(managerWithOrder0)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .build();

        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(fromHubId)).thenReturn(null);
        when(deliveryManagerAssignmentService.assignDeliveryManager(toHubId))
                .thenReturn(managerWithOrder0);
        when(routeFactory.calculateRoute(fromHubId, toHubId, BigDecimal.valueOf(100.5)))
                .thenReturn(new RouteCalculationResult(null, BigDecimal.valueOf(100.5)));
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(savedEntity);

        // when
        DeliveryDto result = deliveryCommandService.createDelivery(command);

        // then
        assertThat(result.getDeliveryManagerId()).isEqualTo(managerWithOrder0);
        verify(deliveryManagerAssignmentService).assignDeliveryManager(toHubId);
    }

    @Test
    @DisplayName("배송 생성 시 업체 배송 담당자(COMPANY)와 각 경로의 허브 배송 담당자(HUB)가 모두 배정된다")
    void createDelivery_shouldAssignBothCompanyAndHubManagers_whenMultipleRoutes() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        
        UUID companyManagerId = UUID.randomUUID();
        UUID hubManagerA = UUID.randomUUID();
        UUID hubManagerB = UUID.randomUUID();

        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .fromHubId(hubA)
                .toHubId(hubC)
                .deliveryManagerId(null) // 자동 배정
                .expectedDistance(BigDecimal.valueOf(100.5))
                .build();

        DeliveryEntity savedEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .fromHubId(hubA)
                .toHubId(hubC)
                .deliveryManagerId(companyManagerId)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .build();

        // 경로: A → B → C
        List<UUID> routePath = List.of(hubA, hubB, hubC);

        HubRouteDto routeAB = HubRouteDto.builder()
                .time(60)
                .distance(100.0)
                .build();
        HubRouteDto routeBC = HubRouteDto.builder()
                .time(90)
                .distance(150.0)
                .build();

        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(hubA)).thenReturn(null);
        when(deliveryManagerAssignmentService.assignDeliveryManager(hubC))
                .thenReturn(companyManagerId);
        
        com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto calculationDto = 
            com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto.builder()
                .path(routePath)
                .totalDistance(250.0)
                .build();
        when(routeCalculationService.calculateShortestPath(hubA, hubC))
                .thenReturn(calculationDto);
        
        when(deliveryRepository.save(any(DeliveryEntity.class))).thenReturn(savedEntity);
        
        when(hubServiceClient.findRouteByDepartAndArrive(hubA, hubB))
                .thenReturn(routeAB);
        when(hubServiceClient.findRouteByDepartAndArrive(hubB, hubC))
                .thenReturn(routeBC);
        when(deliveryManagerAssignmentService.assignDeliveryManager(hubA, "HUB"))
                .thenReturn(hubManagerA);
        when(deliveryManagerAssignmentService.assignDeliveryManager(hubB, "HUB"))
                .thenReturn(hubManagerB);

        DeliveryCommandService serviceWithRealFactory = new DeliveryCommandServiceImpl(
                deliveryRepository,
                routeRepository,
                deliveryValidator,
                realRouteFactory,
                deliveryOutboxService,
                statusValidator,
                messageServiceClient,
                deliveryManagerAssignmentService,
                deliveryManagerServiceClient
        );

        // when
        DeliveryDto result = serviceWithRealFactory.createDelivery(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryManagerId()).isEqualTo(companyManagerId);
        
        verify(deliveryManagerAssignmentService).assignDeliveryManager(hubC);
        verify(deliveryManagerAssignmentService).assignDeliveryManager(hubA, "HUB");
        verify(deliveryManagerAssignmentService).assignDeliveryManager(hubB, "HUB");
    }
}
