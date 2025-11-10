package com.pathfinder.delivery.application.query.service.impl;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.application.query.service.DeliveryQueryService;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryQueryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryQueryServiceImpl implements DeliveryQueryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryQueryRepository deliveryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "delivery", key = "#id")
    public DeliveryDto findById(UUID id) {
        log.debug("Finding delivery by id: {}", id);
        return deliveryRepository.findById(id)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND))
            .toDeliveryDto();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto findByOrderId(UUID orderId) {
        log.debug("Finding delivery by orderId: {}", orderId);
        return deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND))
            .toDeliveryDto();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryDto> searchDeliveries(
        UUID hubId,
        DeliveryStatus status,
        UUID deliveryManagerId,
        Pageable pageable
    ) {
        log.debug("Searching deliveries with filters: hubId={}, status={}, managerId={}", hubId, status, deliveryManagerId);
        
        Page<DeliveryEntity> entityPage = deliveryQueryRepository.searchDeliveries(
            hubId,
            status,
            deliveryManagerId,
            pageable
        );

        return entityPage.map(DeliveryEntity::toDeliveryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDto> findAll() {
        log.debug("Finding all deliveries");
        return deliveryRepository.findAll().stream()
            .map(DeliveryEntity::toDeliveryDto)
            .collect(Collectors.toList());
    }
}
