package com.pathfinder.delivery.application.command.service.impl;

import com.pathfinder.delivery.application.command.service.DeliveryRouteCommandService;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryRouteStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
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
    private final DeliveryManagerServiceClient deliveryManagerServiceClient;
    private final MessageServiceClient messageServiceClient;

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

        DeliveryRouteStatus previousStatus = route.getStatus();
        route.update(command);
        DeliveryRouteStatus newStatus = route.getStatus();

        sendSlackNotificationIfStatusChanged(route, previousStatus, newStatus);

        return route.toDeliveryRouteDto();
    }

    private void sendSlackNotificationIfStatusChanged(
            DeliveryRouteEntity route, 
            DeliveryRouteStatus previousStatus, 
            DeliveryRouteStatus newStatus) {
        
        if (previousStatus == newStatus) {
            return;
        }

        try {
            if (newStatus == DeliveryRouteStatus.PICKED_UP || newStatus == DeliveryRouteStatus.IN_TRANSIT) {
                sendDepartureNotification(route);
            } else if (newStatus == DeliveryRouteStatus.DELIVERED) {
                sendArrivalNotification(route);
            }
        } catch (Exception e) {
            log.error("Failed to send slack notification for route: {}", route.getRouteId(), e);
        }
    }

    private void sendDepartureNotification(DeliveryRouteEntity route) {
        if (route.getFromHubId() == null || route.getToHubId() == null) {
            return;
        }

        ApiResponse<List<DeliveryManagerDto>> fromResponse = 
            deliveryManagerServiceClient.getDeliveryManagersByHubAndType(route.getFromHubId(), "HUB");
        ApiResponse<List<DeliveryManagerDto>> toResponse = 
            deliveryManagerServiceClient.getDeliveryManagersByHubAndType(route.getToHubId(), "HUB");
        List<DeliveryManagerDto> fromHubManagers = fromResponse != null ? fromResponse.getData() : null;
        List<DeliveryManagerDto> toHubManagers = toResponse != null ? toResponse.getData() : null;

        if ((fromHubManagers != null && !fromHubManagers.isEmpty()) && 
            (toHubManagers != null && !toHubManagers.isEmpty())) {
            DeliveryManagerDto senderManager = fromHubManagers.get(0);
            DeliveryManagerDto receiverManager = toHubManagers.get(0);
            String message = buildDepartureMessage(route);
            
            MessageRequestDto messageRequest = MessageRequestDto.builder()
                .request(message)
                .senderId(senderManager.getDeliveryManagerId())
                .receiverId(receiverManager.getDeliveryManagerId())
                .build();

            messageServiceClient.sendSlackMessage(messageRequest);
            log.info("Departure notification sent for route: {} from {} to {}", 
                route.getRouteId(), senderManager.getUsername(), receiverManager.getUsername());
        }
    }

    private void sendArrivalNotification(DeliveryRouteEntity route) {
        if (route.getFromHubId() == null || route.getToHubId() == null) {
            return;
        }

        ApiResponse<List<DeliveryManagerDto>> fromResponse = 
            deliveryManagerServiceClient.getDeliveryManagersByHubAndType(route.getFromHubId(), "HUB");
        ApiResponse<List<DeliveryManagerDto>> toResponse = 
            deliveryManagerServiceClient.getDeliveryManagersByHubAndType(route.getToHubId(), "HUB");
        List<DeliveryManagerDto> fromHubManagers = fromResponse != null ? fromResponse.getData() : null;
        List<DeliveryManagerDto> toHubManagers = toResponse != null ? toResponse.getData() : null;

        if ((fromHubManagers != null && !fromHubManagers.isEmpty()) && 
            (toHubManagers != null && !toHubManagers.isEmpty())) {
            DeliveryManagerDto senderManager = fromHubManagers.get(0);
            DeliveryManagerDto receiverManager = toHubManagers.get(0);
            String message = buildArrivalMessage(route);
            
            MessageRequestDto messageRequest = MessageRequestDto.builder()
                .request(message)
                .senderId(senderManager.getDeliveryManagerId())
                .receiverId(receiverManager.getDeliveryManagerId())
                .build();

            messageServiceClient.sendSlackMessage(messageRequest);
            log.info("Arrival notification sent for route: {} from {} to {}", 
                route.getRouteId(), senderManager.getUsername(), receiverManager.getUsername());
        }
    }

    private String buildDepartureMessage(DeliveryRouteEntity route) {
        return String.format(
            "배송이 출발했습니다.\n" +
            "경로 ID: %s\n" +
            "출발 허브: %s\n" +
            "도착 허브: %s\n" +
            "시퀀스: %d",
            route.getRouteId(),
            route.getFromHubId(),
            route.getToHubId(),
            route.getSequence()
        );
    }

    private String buildArrivalMessage(DeliveryRouteEntity route) {
        return String.format(
            "배송이 도착했습니다.\n" +
            "경로 ID: %s\n" +
            "출발 허브: %s\n" +
            "도착 허브: %s\n" +
            "시퀀스: %d",
            route.getRouteId(),
            route.getFromHubId(),
            route.getToHubId(),
            route.getSequence()
        );
    }
}

