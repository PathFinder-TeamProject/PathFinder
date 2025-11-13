package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryManagerAssignmentRepository;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryManagerAssignmentServiceTest {

    @Mock
    private DeliveryManagerServiceClient deliveryManagerServiceClient;

    @Mock
    private DeliveryManagerAssignmentRepository assignmentRepository;

    private DeliveryManagerAssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        assignmentService = new DeliveryManagerAssignmentService(
                deliveryManagerServiceClient,
                assignmentRepository
        );
    }

    @Test
    @DisplayName("첫 배정 시 deliveryOrder가 0인 담당자를 배정한다")
    void assignDeliveryManager_shouldAssignOrder0_whenFirstAssignment() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID manager0Id = UUID.randomUUID();
        UUID manager1Id = UUID.randomUUID();

        DeliveryManagerDto dto0 = createManagerDto(manager0Id, 0);
        DeliveryManagerDto dto1 = createManagerDto(manager1Id, 1);

        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(hubId, "COMPANY"))
                .thenReturn(ApiResponse.success(List.of(dto1, dto0)));
        when(assignmentRepository.getLastAssignedOrder(hubId)).thenReturn(null);

        // when
        UUID assignedId = assignmentService.assignDeliveryManager(hubId);

        // then
        assertThat(assignedId).isEqualTo(manager0Id);
        verify(assignmentRepository).saveLastAssignedOrder(hubId, 0);
    }

            @Test
            @DisplayName("Round-Robin: 마지막 배정 순번이 0이면 다음 순번 1을 배정한다")
            void assignDeliveryManager_shouldAssignNextOrder_whenRoundRobin() {
                // given
                UUID hubId = UUID.randomUUID();
                UUID manager0Id = UUID.randomUUID();
                UUID manager1Id = UUID.randomUUID();
                UUID manager2Id = UUID.randomUUID();

                DeliveryManagerDto dto0 = createManagerDto(manager0Id, 0);
                DeliveryManagerDto dto1 = createManagerDto(manager1Id, 1);
                DeliveryManagerDto dto2 = createManagerDto(manager2Id, 2);

                when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(hubId, "COMPANY"))
                        .thenReturn(ApiResponse.success(List.of(dto2, dto0, dto1)));
                when(assignmentRepository.getLastAssignedOrder(hubId)).thenReturn(0);

                // when
                UUID assignedId = assignmentService.assignDeliveryManager(hubId);

                // then
                assertThat(assignedId).isEqualTo(manager1Id);
        verify(assignmentRepository).saveLastAssignedOrder(hubId, 1);
    }

            @Test
            @DisplayName("Round-Robin: 마지막 순번이 최대값이면 다시 0으로 돌아간다")
            void assignDeliveryManager_shouldReturnToOrder0_whenLastOrderIsMax() {
                // given
                UUID hubId = UUID.randomUUID();
                UUID manager0Id = UUID.randomUUID();
                UUID manager2Id = UUID.randomUUID();

                DeliveryManagerDto dto0 = createManagerDto(manager0Id, 0);
                DeliveryManagerDto dto2 = createManagerDto(manager2Id, 2);

                when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(hubId, "COMPANY"))
                        .thenReturn(ApiResponse.success(List.of(dto2, dto0)));
                when(assignmentRepository.getLastAssignedOrder(hubId)).thenReturn(2);

                // when
                UUID assignedId = assignmentService.assignDeliveryManager(hubId);

                // then
                assertThat(assignedId).isEqualTo(manager0Id);
        verify(assignmentRepository).saveLastAssignedOrder(hubId, 0);
    }

    @Test
    @DisplayName("배송담당자가 없으면 예외를 발생시킨다")
    void assignDeliveryManager_shouldThrowException_whenNoManagers() {
        // given
        UUID hubId = UUID.randomUUID();

        when(deliveryManagerServiceClient.getDeliveryManagersByHubAndType(hubId, "COMPANY"))
                .thenReturn(ApiResponse.success(List.of()));

        // when & then
        assertThatThrownBy(() -> assignmentService.assignDeliveryManager(hubId))
                .isInstanceOf(PathException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
    }

    private DeliveryManagerDto createManagerDto(UUID managerId, Integer deliveryOrder) {
        return DeliveryManagerDto.builder()
                .deliveryManagerId(managerId)
                .username("manager" + managerId)
                .deliveryOrder(deliveryOrder)
                .hubId(UUID.randomUUID())
                .type("COMPANY")
                .build();
    }
}

