package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteQueryService {

    private final DeliveryRouteRepository routeRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "deliveryRoutes", key = "#routeId")
    public DeliveryRouteDto findById(UUID routeId) {
        log.debug("Finding delivery route by id: {}", routeId);
        return routeRepository.findById(routeId)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.ROUTE_NOT_FOUND))
            .toDeliveryRouteDto();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "deliveryRoutes", key = "#deliveryId")
    public List<DeliveryRouteDto> findByDeliveryId(UUID deliveryId) {
        log.debug("Finding delivery routes by deliveryId: {}", deliveryId);
        return routeRepository.findByDeliveryIdOrderBySequence(deliveryId).stream()
            .map(DeliveryRouteEntity::toDeliveryRouteDto)
            .collect(Collectors.toList());
    }
}

