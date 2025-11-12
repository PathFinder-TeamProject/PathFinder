package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryEntityTest {

    @Test
    @DisplayName("배송 상태가 변경될 때 updateWithCommand는 true를 반환한다")
    void updateWithCommand_shouldReturnTrue_whenStatusChanged() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.READY)
                .build();

        UpdateDeliveryCommandDto command = UpdateDeliveryCommandDto.builder()
                .deliveryId(deliveryId)
                .status("IN_TRANSIT")
                .build();

        // when
        boolean statusChanged = delivery.updateWithCommand(command, DeliveryStatus.IN_TRANSIT);

        // then
        assertThat(statusChanged).isTrue();
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("배송 상태가 변경되지 않을 때 updateWithCommand는 false를 반환한다")
    void updateWithCommand_shouldReturnFalse_whenStatusNotChanged() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.READY)
                .build();

        UpdateDeliveryCommandDto command = UpdateDeliveryCommandDto.builder()
                .expectedDistance(BigDecimal.valueOf(100.5))
                .build();

        // when
        boolean statusChanged = delivery.updateWithCommand(command, null);

        // then
        assertThat(statusChanged).isFalse();
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.READY);
        assertThat(delivery.getExpectedDistance()).isEqualTo(BigDecimal.valueOf(100.5));
    }

    @Test
    @DisplayName("cancel 메서드는 배송 상태를 CANCELLED로 변경한다")
    void cancel_shouldChangeStatusToCancelled() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.IN_TRANSIT)
                .build();

        // when
        delivery.cancel();

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
    }

    @Test
    @DisplayName("이미 취소된 배송의 cancel 메서드는 상태를 변경하지 않는다")
    void cancel_shouldNotChangeStatus_whenAlreadyCancelled() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .status(DeliveryStatus.CANCELLED)
                .build();

        // when
        delivery.cancel();

        // then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
    }

    @Test
    @DisplayName("determineUpdateEventType은 상태 변경 시 STATUS_CHANGED를 반환한다")
    void determineUpdateEventType_shouldReturnStatusChanged_whenStatusChanged() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .build();

        // when
        String eventType = delivery.determineUpdateEventType(true);

        // then
        assertThat(eventType).isEqualTo("STATUS_CHANGED");
    }

    @Test
    @DisplayName("determineUpdateEventType은 상태 변경 없을 시 UPDATED를 반환한다")
    void determineUpdateEventType_shouldReturnUpdated_whenStatusNotChanged() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryEntity delivery = DeliveryEntity.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .deliveryManagerId(deliveryManagerId)
                .build();

        // when
        String eventType = delivery.determineUpdateEventType(false);

        // then
        assertThat(eventType).isEqualTo("UPDATED");
    }
}
