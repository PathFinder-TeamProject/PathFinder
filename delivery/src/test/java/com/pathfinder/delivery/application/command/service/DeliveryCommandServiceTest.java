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
import static org.mockito.Mockito.*;

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
    private DeliveryOutboxService deliveryOutboxService;

    @Mock
    private DeliveryStatusValidator statusValidator;

    private DeliveryCommandService deliveryCommandService;

    @BeforeEach
    void setUp() {
        deliveryCommandService = new DeliveryCommandServiceImpl(
                deliveryRepository,
                routeRepository,
                deliveryValidator,
                routeFactory,
                deliveryOutboxService,
                statusValidator
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

        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(orderId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(deliveryManagerId)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .receiverSlackId("hong@example.com")
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

        when(deliveryValidator.validateAndGetOrder(orderId)).thenReturn(null);
        when(deliveryValidator.validateAndGetHub(fromHubId)).thenReturn(null);
        when(deliveryValidator.validateAndGetDeliveryManager(deliveryManagerId)).thenReturn(null);
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
        CreateDeliveryCommandDto command = CreateDeliveryCommandDto.builder()
                .orderId(UUID.randomUUID())
                .deliveryManagerId(UUID.randomUUID())
                .build();

        when(deliveryRepository.save(any(DeliveryEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThatThrownBy(() -> deliveryCommandService.createDelivery(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(deliveryRepository).save(any(DeliveryEntity.class));
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
        verify(deliveryRepository).save(any(DeliveryEntity.class));
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
}
