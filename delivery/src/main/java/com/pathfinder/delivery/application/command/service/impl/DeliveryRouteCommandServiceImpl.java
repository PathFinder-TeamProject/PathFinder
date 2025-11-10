package com.pathfinder.delivery.application.command.service.impl;

import com.pathfinder.delivery.application.command.service.DeliveryRouteCommandService;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteCommandServiceImpl implements DeliveryRouteCommandService {

    private final DeliveryRouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional
    @CacheEvict(value = "deliveryRoutesByDeliveryId", key = "#command.deliveryId")
    public DeliveryRouteDto createRoute(CreateDeliveryRouteCommandDto command) {
        log.info("Creating delivery route for deliveryId: {}", command.getDeliveryId());

        DeliveryEntity delivery = deliveryRepository.findById(command.getDeliveryId())
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        List<DeliveryRouteEntity> existingRoutes = routeRepository.findByDeliveryIdOrderBySequence(command.getDeliveryId());
        int nextSequence = existingRoutes.isEmpty() ? 0 : existingRoutes.size();

        DeliveryRouteEntity route = command.toEntity(nextSequence);
        DeliveryRouteEntity savedRoute = routeRepository.save(route);

        return savedRoute.toDeliveryRouteDto();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "deliveryRouteById", key = "#routeId"),
        @CacheEvict(value = "deliveryRoutesByDeliveryId", key = "#result.deliveryId")
    })
    public DeliveryRouteDto updateRoute(UUID routeId, CreateDeliveryRouteCommandDto command) {
        log.info("Updating delivery route: {}", routeId);

        DeliveryRouteEntity route = routeRepository.findById(routeId)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.ROUTE_NOT_FOUND));

        route.update(command);

        return route.toDeliveryRouteDto();
    }
}

