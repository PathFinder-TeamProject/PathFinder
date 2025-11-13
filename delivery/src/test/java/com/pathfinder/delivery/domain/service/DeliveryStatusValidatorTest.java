package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.global.presentation.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryStatusValidatorTest {

    private DeliveryStatusValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DeliveryStatusValidator();
    }

    @Test
    @DisplayName("READY 상태에서 IN_PROGRESS로 전이는 유효하다")
    void validateStatusTransition_shouldPass_whenReadyToInProgress() {
        // when & then
        assertThatCode(() -> validator.validateStatusTransition(DeliveryStatus.READY, DeliveryStatus.IN_PROGRESS))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("READY 상태에서 CANCELLED로 전이는 유효하다")
    void validateStatusTransition_shouldPass_whenReadyToCancelled() {
        // when & then
        assertThatCode(() -> validator.validateStatusTransition(DeliveryStatus.READY, DeliveryStatus.CANCELLED))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("READY 상태에서 DONE으로 전이는 유효하지 않다")
    void validateStatusTransition_shouldThrowException_whenReadyToDone() {
        // when & then
        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.READY, DeliveryStatus.DONE))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
    }

    @Test
    @DisplayName("READY 상태에서 IN_TRANSIT로 전이는 유효하지 않다")
    void validateStatusTransition_shouldThrowException_whenReadyToInTransit() {
        // when & then
        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.READY, DeliveryStatus.IN_TRANSIT))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
    }

    @Test
    @DisplayName("IN_PROGRESS 상태에서 DONE으로 전이는 유효하다")
    void validateStatusTransition_shouldPass_whenInProgressToDone() {
        // when & then
        assertThatCode(() -> validator.validateStatusTransition(DeliveryStatus.IN_PROGRESS, DeliveryStatus.DONE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("IN_PROGRESS 상태에서 CANCELLED로 전이는 유효하다")
    void validateStatusTransition_shouldPass_whenInProgressToCancelled() {
        // when & then
        assertThatCode(() -> validator.validateStatusTransition(DeliveryStatus.IN_PROGRESS, DeliveryStatus.CANCELLED))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("IN_PROGRESS 상태에서 READY로 전이는 유효하지 않다")
    void validateStatusTransition_shouldThrowException_whenInProgressToReady() {
        // when & then
        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.IN_PROGRESS, DeliveryStatus.READY))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
    }

    @Test
    @DisplayName("DONE 상태에서 다른 상태로 전이는 불가능하다")
    void validateStatusTransition_shouldThrowException_whenDoneToAnyStatus() {
        // when & then
        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.DONE, DeliveryStatus.READY))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_ALREADY_COMPLETED);

        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.DONE, DeliveryStatus.IN_PROGRESS))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_ALREADY_COMPLETED);
    }

    @Test
    @DisplayName("CANCELLED 상태에서 다른 상태로 전이는 불가능하다")
    void validateStatusTransition_shouldThrowException_whenCancelledToAnyStatus() {
        // when & then
        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.CANCELLED, DeliveryStatus.READY))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_ALREADY_CANCELLED);

        assertThatThrownBy(() -> validator.validateStatusTransition(DeliveryStatus.CANCELLED, DeliveryStatus.IN_PROGRESS))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_ALREADY_CANCELLED);
    }
}

