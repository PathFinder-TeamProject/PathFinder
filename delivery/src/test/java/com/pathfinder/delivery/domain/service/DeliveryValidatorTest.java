package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.ApiResponseDto;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryValidatorTest {

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private HubServiceClient hubServiceClient;

    @Mock
    private DeliveryManagerServiceClient deliveryManagerServiceClient;

    private DeliveryValidator deliveryValidator;

    @BeforeEach
    void setUp() {
        deliveryValidator = new DeliveryValidator(
                orderServiceClient,
                hubServiceClient,
                deliveryManagerServiceClient
        );
    }

    @Test
    @DisplayName("주문 검증 시 유효한 주문이면 주문 정보를 반환한다")
    void validateAndGetOrder_shouldReturnOrder_whenValidOrder() {
        // given
        UUID orderId = UUID.randomUUID();
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .productId(UUID.randomUUID())
                .orderStatus("PENDING")
                .build();

        ApiResponseDto<OrderDto> apiResponse = new ApiResponseDto<>("SUCCESS", "조회 성공", orderDto);
        ResponseEntity<ApiResponseDto<OrderDto>> response = ResponseEntity.ok(apiResponse);

        when(orderServiceClient.getOrder(orderId)).thenReturn(response);

        // when
        OrderDto result = deliveryValidator.validateAndGetOrder(orderId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("주문 검증 시 응답이 null이면 예외를 발생시킨다")
    void validateAndGetOrder_shouldThrowException_whenResponseIsNull() {
        // given
        UUID orderId = UUID.randomUUID();
        when(orderServiceClient.getOrder(orderId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetOrder(orderId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 검증 시 응답 body가 null이면 예외를 발생시킨다")
    void validateAndGetOrder_shouldThrowException_whenResponseBodyIsNull() {
        // given
        UUID orderId = UUID.randomUUID();
        ResponseEntity<ApiResponseDto<OrderDto>> response = ResponseEntity.ok(null);
        when(orderServiceClient.getOrder(orderId)).thenReturn(response);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetOrder(orderId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("주문 검증 시 응답 data가 null이면 예외를 발생시킨다")
    void validateAndGetOrder_shouldThrowException_whenResponseDataIsNull() {
        // given
        UUID orderId = UUID.randomUUID();
        ApiResponseDto<OrderDto> apiResponse = new ApiResponseDto<>("SUCCESS", "조회 성공", null);
        ResponseEntity<ApiResponseDto<OrderDto>> response = ResponseEntity.ok(apiResponse);
        when(orderServiceClient.getOrder(orderId)).thenReturn(response);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetOrder(orderId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("허브 검증 시 유효한 허브이면 허브 정보를 반환한다")
    void validateAndGetHub_shouldReturnHub_whenValidHub() {
        // given
        UUID hubId = UUID.randomUUID();
        HubDto hubDto = HubDto.builder()
                .hubId(hubId)
                .hubName("서울 허브")
                .hubAddress("서울시 강남구")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        when(hubServiceClient.getHub(hubId)).thenReturn(hubDto);

        // when
        HubDto result = deliveryValidator.validateAndGetHub(hubId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getHubId()).isEqualTo(hubId);
        assertThat(result.getHubName()).isEqualTo("서울 허브");
    }

    @Test
    @DisplayName("허브 검증 시 hubId가 null이면 null을 반환한다")
    void validateAndGetHub_shouldReturnNull_whenHubIdIsNull() {
        // when
        HubDto result = deliveryValidator.validateAndGetHub(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("허브 검증 시 허브가 null이면 예외를 발생시킨다")
    void validateAndGetHub_shouldThrowException_whenHubIsNull() {
        // given
        UUID hubId = UUID.randomUUID();
        when(hubServiceClient.getHub(hubId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetHub(hubId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.HUB_NOT_FOUND);
    }

    @Test
    @DisplayName("배송 담당자 검증 시 유효한 담당자이면 담당자 정보를 반환한다")
    void validateAndGetDeliveryManager_shouldReturnManager_whenValidManager() {
        // given
        UUID managerId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        DeliveryManagerDto managerDto = DeliveryManagerDto.builder()
                .deliveryManagerId(managerId)
                .username("testuser")
                .type("COMPANY")
                .deliveryOrder(0)
                .hubId(hubId)
                .slackId("U12345")
                .build();

        ApiResponse<DeliveryManagerDto> apiResponse = ApiResponse.success(managerDto);

        when(deliveryManagerServiceClient.getDeliveryManager(managerId)).thenReturn(apiResponse);

        // when
        DeliveryManagerDto result = deliveryValidator.validateAndGetDeliveryManager(managerId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveryManagerId()).isEqualTo(managerId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getType()).isEqualTo("COMPANY");
    }

    @Test
    @DisplayName("배송 담당자 검증 시 응답이 null이면 예외를 발생시킨다")
    void validateAndGetDeliveryManager_shouldThrowException_whenResponseIsNull() {
        // given
        UUID managerId = UUID.randomUUID();
        when(deliveryManagerServiceClient.getDeliveryManager(managerId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetDeliveryManager(managerId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
    }

    @Test
    @DisplayName("배송 담당자 검증 시 응답 data가 null이면 예외를 발생시킨다")
    void validateAndGetDeliveryManager_shouldThrowException_whenResponseDataIsNull() {
        // given
        UUID managerId = UUID.randomUUID();
        ApiResponse<DeliveryManagerDto> apiResponse = ApiResponse.success(null);

        when(deliveryManagerServiceClient.getDeliveryManager(managerId)).thenReturn(apiResponse);

        // when & then
        assertThatThrownBy(() -> deliveryValidator.validateAndGetDeliveryManager(managerId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
    }
}

