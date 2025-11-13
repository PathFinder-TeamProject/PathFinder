package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryManagerAssignmentRepository;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryManagerAssignmentService {

    private final DeliveryManagerServiceClient deliveryManagerServiceClient;
    private final DeliveryManagerAssignmentRepository assignmentRepository;

    public UUID assignDeliveryManager(UUID hubId) {
        return assignDeliveryManager(hubId, "COMPANY");
    }

    public UUID assignDeliveryManager(UUID hubId, String type) {
        log.info("Assigning {} delivery manager automatically for hubId: {}", type, hubId);
        
        ApiResponse<List<DeliveryManagerDto>> response = deliveryManagerServiceClient.getDeliveryManagersByHubAndType(hubId, type);
        List<DeliveryManagerDto> managers = response != null ? response.getData() : null;
        
        if (managers == null || managers.isEmpty()) {
            log.warn("No {} delivery managers found for hubId: {}", type, hubId);
            throw new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        Integer lastOrder = assignmentRepository.getLastAssignedOrder(hubId);
        Integer nextOrder = calculateNextOrder(managers, lastOrder);
        
        UUID assignedManagerId = findManagerByOrder(managers, nextOrder)
                .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND));

        assignmentRepository.saveLastAssignedOrder(hubId, nextOrder);
        
        log.info("Assigned {} delivery manager: {} (order: {}) for hubId: {}", type, assignedManagerId, nextOrder, hubId);
        return assignedManagerId;
    }

    private Integer calculateNextOrder(List<DeliveryManagerDto> managers, Integer lastOrder) {
        if (lastOrder == null) {
            return managers.stream()
                    .map(DeliveryManagerDto::getDeliveryOrder)
                    .filter(order -> order != null)
                    .min(Comparator.naturalOrder())
                    .orElse(0);
        }

        Integer maxOrder = managers.stream()
                .map(DeliveryManagerDto::getDeliveryOrder)
                .filter(order -> order != null)
                .max(Comparator.naturalOrder())
                .orElse(0);

        Integer nextOrder = managers.stream()
                .map(DeliveryManagerDto::getDeliveryOrder)
                .filter(order -> order != null && order > lastOrder)
                .min(Comparator.naturalOrder())
                .orElse(null);

        if (nextOrder == null) {
            return managers.stream()
                    .map(DeliveryManagerDto::getDeliveryOrder)
                    .filter(order -> order != null)
                    .min(Comparator.naturalOrder())
                    .orElse(0);
        }

        return nextOrder;
    }

    private Optional<UUID> findManagerByOrder(List<DeliveryManagerDto> managers, Integer order) {
        return managers.stream()
                .filter(manager -> order.equals(manager.getDeliveryOrder()))
                .findFirst()
                .map(DeliveryManagerDto::getDeliveryManagerId);
    }
}

