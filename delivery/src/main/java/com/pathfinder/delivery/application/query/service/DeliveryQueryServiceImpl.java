package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        
        List<DeliveryEntity> allDeliveries = deliveryRepository.findAll();
        
        // 필터링
        List<DeliveryEntity> filtered = allDeliveries.stream()
            .filter(d -> hubId == null || d.getFromHubId() != null && d.getFromHubId().equals(hubId) ||  d.getToHubId() != null && d.getToHubId().equals(hubId))
            .filter(d -> status == null || d.getStatus() == status)
            .filter(d -> deliveryManagerId == null || d.getDeliveryManagerId().equals(deliveryManagerId))
            .collect(Collectors.toList());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<DeliveryEntity> paged = start < filtered.size() ? filtered.subList(start, end) : List.of();

        List<DeliveryDto> dtos = paged.stream()
            .map(DeliveryEntity::toDeliveryDto)
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, filtered.size());
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
