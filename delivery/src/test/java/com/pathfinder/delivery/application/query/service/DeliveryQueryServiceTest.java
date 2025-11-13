package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.application.query.service.impl.DeliveryQueryServiceImpl;
import com.pathfinder.delivery.domain.repository.DeliveryQueryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryQueryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryQueryRepository deliveryQueryRepository;

    private DeliveryQueryService deliveryQueryService;

    @BeforeEach
    void setUp() {
        deliveryQueryService = new DeliveryQueryServiceImpl(deliveryRepository, deliveryQueryRepository);
    }

    @Test
    @DisplayName("ID로 배송을 조회하면 배송 정보를 반환한다")
    void findById_shouldReturnDeliveryDto_whenDeliveryExists() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity deliveryEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.READY)
                .expectedDistance(BigDecimal.valueOf(100.5))
                .deliveryAddress("서울시 강남구")
                .receiverName("홍길동")
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(deliveryEntity));

        // when
        DeliveryDto result = deliveryQueryService.findById(deliveryId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getDeliveryManagerId()).isEqualTo(deliveryManagerId);
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.READY);
        assertThat(result.getExpectedDistance()).isEqualTo(BigDecimal.valueOf(100.5));
        assertThat(result.getDeliveryAddress()).isEqualTo("서울시 강남구");
        assertThat(result.getReceiverName()).isEqualTo("홍길동");

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 배송을 조회하면 예외를 발생시킨다")
    void findById_shouldThrowException_whenDeliveryNotFound() {
        // given
        UUID deliveryId = UUID.randomUUID();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryQueryService.findById(deliveryId))
                .isInstanceOf(RuntimeException.class);

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    @DisplayName("주문 ID로 배송을 조회하면 배송 정보를 반환한다")
    void findByOrderId_shouldReturnDeliveryDto_whenDeliveryExists() {
        // given
        UUID orderId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity deliveryEntity = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.IN_TRANSIT)
                .actualDistance(BigDecimal.valueOf(85.0))
                .build();

        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(deliveryEntity));

        // when
        DeliveryDto result = deliveryQueryService.findByOrderId(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
        assertThat(result.getActualDistance()).isEqualTo(BigDecimal.valueOf(85.0));

        verify(deliveryRepository).findByOrderId(orderId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 배송을 조회하면 예외를 발생시킨다")
    void findByOrderId_shouldThrowException_whenDeliveryNotFound() {
        // given
        UUID orderId = UUID.randomUUID();

        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryQueryService.findByOrderId(orderId))
                .isInstanceOf(RuntimeException.class);

        verify(deliveryRepository).findByOrderId(orderId);
    }

    @Test
    @DisplayName("배송 검색 시 조건에 맞는 배송 목록을 페이징하여 반환한다")
    void searchDeliveries_shouldReturnPagedDeliveryDtos_whenSearchConditionsProvided() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        List<DeliveryEntity> deliveryEntities = Arrays.asList(
                DeliveryEntity.builder()
                        .deliveryId(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .fromHubId(hubId)
                        .deliveryManagerId(deliveryManagerId)
                        .status(DeliveryStatus.READY)
                        .build(),
                DeliveryEntity.builder()
                        .deliveryId(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .toHubId(hubId)
                        .deliveryManagerId(deliveryManagerId)
                        .status(DeliveryStatus.IN_TRANSIT)
                        .build()
        );

        Page<DeliveryEntity> deliveryPage = new PageImpl<>(deliveryEntities, pageable, 2);

        when(deliveryQueryRepository.searchDeliveries(hubId, DeliveryStatus.READY, deliveryManagerId, pageable))
                .thenReturn(deliveryPage);

        // when
        Page<DeliveryDto> result = deliveryQueryService.searchDeliveries(hubId, DeliveryStatus.READY, deliveryManagerId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getFromHubId()).isEqualTo(hubId);
        assertThat(result.getContent().get(0).getDeliveryManagerId()).isEqualTo(deliveryManagerId);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(DeliveryStatus.READY);
        assertThat(result.getContent().get(1).getToHubId()).isEqualTo(hubId);
        assertThat(result.getContent().get(1).getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);

        verify(deliveryQueryRepository).searchDeliveries(hubId, DeliveryStatus.READY, deliveryManagerId, pageable);
    }

    @Test
    @DisplayName("모든 배송을 조회하면 배송 목록을 반환한다")
    void findAll_shouldReturnAllDeliveries() {
        // given
        List<DeliveryEntity> deliveryEntities = Arrays.asList(
                DeliveryEntity.builder()
                        .deliveryId(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .status(DeliveryStatus.READY)
                        .build(),
                DeliveryEntity.builder()
                        .deliveryId(UUID.randomUUID())
                        .orderId(UUID.randomUUID())
                        .status(DeliveryStatus.DONE)
                        .build()
        );

        when(deliveryRepository.findAll()).thenReturn(deliveryEntities);

        // when
        List<DeliveryDto> result = deliveryQueryService.findAll();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(DeliveryStatus.READY);
        assertThat(result.get(1).getStatus()).isEqualTo(DeliveryStatus.DONE);

        verify(deliveryRepository).findAll();
    }

    @Test
    @DisplayName("빈 배송 목록을 조회하면 빈 리스트를 반환한다")
    void findAll_shouldReturnEmptyList_whenNoDeliveriesExist() {
        // given
        when(deliveryRepository.findAll()).thenReturn(Arrays.asList());

        // when
        List<DeliveryDto> result = deliveryQueryService.findAll();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(deliveryRepository).findAll();
    }
}
