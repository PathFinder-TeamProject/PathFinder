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
public class DeliveryManagerServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubCacheRepository hubCacheRepository;

    @Transactional
    public DeliveryManagerResponseDto createDeliveryManager(DeliveryManagerRequestDto dto) {
        // 허브 캐시 기반 존재 여부 확인
        if (!hubCacheRepository.exists(dto.getHubId())) {
            throw new IllegalArgumentException("허브 서비스에서 존재하지 않는 허브입니다.");
        }

        int order = (dto.getDeliveryOrder() != null)
                ? dto.getDeliveryOrder()
                : deliveryManagerRepository.countByHubId(dto.getHubId());

        DeliveryManagerEntity deliveryManager = DeliveryManagerEntity.create(dto);

        DeliveryManagerEntity saved = deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerResponseDto.of(saved);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManager(Long id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        return DeliveryManagerResponseDto.of(deliveryManager);
    }
    @Transactional
    public void deleteManager(Long id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
//        deliveryManager.setDeletedAt(LocalDateTime.now());
//        deliveryManager.setDeletedBy("system");
    }
    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponseDto> getAllManagers(Long hubId, int page, int size, String sortBy, boolean isAsc) {
        Page<DeliveryManagerEntity> deliveryManagerPage;
        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        if (hubId != null) {
            deliveryManagerPage = deliveryManagerRepository.findByHubId(hubId, pageable);
        } else {
            deliveryManagerPage = deliveryManagerRepository.findAll(pageable);
        }
        return deliveryManagerPage.map(DeliveryManagerResponseDto::forList);
    }
}