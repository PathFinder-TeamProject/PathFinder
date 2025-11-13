package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryRouteEntityTest {

    @Test
    @DisplayName("updateStatus는 경로 상태와 발생 시간을 업데이트한다")
    void updateStatus_shouldUpdateStatusAndOccurredAt() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        DeliveryRouteEntity route = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .status(DeliveryRouteStatus.READY)
                .occurredAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);

        // when
        route.updateStatus(DeliveryRouteStatus.PICKED_UP);

        // then
        assertThat(route.getStatus()).isEqualTo(DeliveryRouteStatus.PICKED_UP);
        assertThat(route.getOccurredAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("updateActualMetrics는 실제 시간과 거리를 업데이트한다")
    void updateActualMetrics_shouldUpdateActualTimeAndDistance() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        DeliveryRouteEntity route = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .actualTime(null)
                .actualDistance(null)
                .build();

        // when
        route.updateActualMetrics(120, BigDecimal.valueOf(150.5));

        // then
        assertThat(route.getActualTime()).isEqualTo(120);
        assertThat(route.getActualDistance()).isEqualTo(BigDecimal.valueOf(150.5));
    }

    @Test
    @DisplayName("updateExpectedMetrics는 예상 시간과 거리를 업데이트한다")
    void updateExpectedMetrics_shouldUpdateExpectedTimeAndDistance() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        DeliveryRouteEntity route = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .expectedTime(null)
                .expectedDistance(null)
                .build();

        // when
        route.updateExpectedMetrics(100, BigDecimal.valueOf(120.0));

        // then
        assertThat(route.getExpectedTime()).isEqualTo(100);
        assertThat(route.getExpectedDistance()).isEqualTo(BigDecimal.valueOf(120.0));
    }

    @Test
    @DisplayName("update 메서드는 모든 필드를 업데이트한다")
    void update_shouldUpdateAllFieldsFromCommand() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryRouteEntity route = DeliveryRouteEntity.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .status(DeliveryRouteStatus.READY)
                .build();

        CreateDeliveryRouteCommandDto command = CreateDeliveryRouteCommandDto.builder()
                .status(DeliveryRouteStatus.IN_TRANSIT)
                .actualTime(90)
                .actualDistance(BigDecimal.valueOf(80.5))
                .expectedTime(85)
                .expectedDistance(BigDecimal.valueOf(75.0))
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .deliveryManagerId(deliveryManagerId)
                .note("Updated note")
                .build();

        // when
        route.update(command);

        // then
        assertThat(route.getStatus()).isEqualTo(DeliveryRouteStatus.IN_TRANSIT);
        assertThat(route.getActualTime()).isEqualTo(90);
        assertThat(route.getActualDistance()).isEqualTo(BigDecimal.valueOf(80.5));
        assertThat(route.getExpectedTime()).isEqualTo(85);
        assertThat(route.getExpectedDistance()).isEqualTo(BigDecimal.valueOf(75.0));
        assertThat(route.getFromHubId()).isEqualTo(fromHubId);
        assertThat(route.getToHubId()).isEqualTo(toHubId);
        assertThat(route.getDeliveryManagerId()).isEqualTo(deliveryManagerId);
        assertThat(route.getNote()).isEqualTo("Updated note");
    }
}
