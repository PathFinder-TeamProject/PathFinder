package com.pathfinder.delivery_manager.application;

import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.repository.DeliveryManagerRepository;
import com.pathfinder.delivery_manager.infrastructure.cache.HubCacheRepository;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubCacheRepository hubCacheRepository;

    public DeliveryManagerResponseDto createManager(DeliveryManagerRequestDto dto) {
        // 허브 캐시 기반 존재 여부 확인
        if (!hubCacheRepository.exists(dto.getHubId())) {
            throw new IllegalArgumentException("허브 서비스에서 존재하지 않는 허브입니다.");
        }

        int order = (dto.getDeliveryOrder() != null)
                ? dto.getDeliveryOrder()
                : deliveryManagerRepository.countByHubId(dto.getHubId());

        DeliveryManagerEntity entity = DeliveryManagerEntity.builder()
                .username(dto.getUsername())
                .hubId(dto.getHubId())
                .type(dto.getType())
                .deliveryOrder(order)
                .build();

        DeliveryManagerEntity saved = deliveryManagerRepository.save(entity);
        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManager(Long id) {
        DeliveryManagerEntity entity = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        return toResponseDto(entity);
    }

    public void deleteManager(Long id) {
        DeliveryManagerEntity entity = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
//        entity.setDeletedAt(LocalDateTime.now());
//        entity.setDeletedBy("system");
    }

    private DeliveryManagerResponseDto toResponseDto(DeliveryManagerEntity entity) {
        return DeliveryManagerResponseDto.builder()
                .deliveryManagerId(entity.getDeliveryManagerId())
                .username(entity.getUsername())
                .hubId(entity.getHubId())
                .type(entity.getType())
                .deliveryOrder(entity.getDeliveryOrder())
                .build();
    }

    public Page<DeliveryManagerResponseDto> getAllManagers(Long hubId, int page, int size, String sortBy, boolean isAsc) {
        Page<DeliveryManagerEntity> entities;
        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (hubId != null) {
            entities = deliveryManagerRepository.findByHubId(hubId, pageable);
        } else {
            entities = deliveryManagerRepository.findAll(pageable);
        }
        return entities.map(this::toResponseDto);
    }
}