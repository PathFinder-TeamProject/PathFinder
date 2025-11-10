package com.pathfinder.delivery_manager.application;

import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.repository.DeliveryManagerRepository;
import com.pathfinder.delivery_manager.infrastructure.cache.HubCacheRepository;
import com.pathfinder.delivery_manager.infrastructure.security.JwtUserContext;
import com.pathfinder.delivery_manager.kafka.UserRequestProducer;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import com.pathfinder.global.event.NewDeliveryManagerEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubCacheRepository hubCacheRepository;
    private final UserRequestProducer userRequestProducer;

    @Transactional
    public void createDeliveryManager(DeliveryManagerRequestDto dto) {
        // 허브 캐시 기반 존재 여부 확인
    /*    if (!hubCacheRepository.exists(dto.getHubId())) {
            throw new IllegalArgumentException("허브 서비스에서 존재하지 않는 허브입니다.");
        }*/
        log.info("Inside createDeliveryManager method - DeliveryManager ID: {}", dto);
        if (deliveryManagerRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.info("배송 담당자 이미 존재 - Username: {}", dto.getUsername());
            return;
        }
        dto.setDeliveryOrder(deliveryManagerRepository.countByHubId(dto.getHubId()));
        DeliveryManagerEntity deliveryManager = DeliveryManagerEntity.create(dto);
        deliveryManager.setCreate(Instant.now(), deliveryManager.getUsername());
        DeliveryManagerEntity saved = deliveryManagerRepository.save(deliveryManager);
//        return DeliveryManagerResponseDto.of(saved);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerById(Long id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));

        // 2. Kafka Request-Reply를 사용하여 User Service에 사용자 정보 요청 및 응답 받기
        UserInfoDto userInfoDto = userRequestProducer.requestUserInfo(deliveryManager.getUsername());

        // 3. 응답 받은 정보를 DTO에 담아 반환
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }
    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerByUsername(String username) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        // 2. Kafka Request-Reply를 사용하여 User Service에 사용자 정보 요청 및 응답 받기
        UserInfoDto userInfoDto = userRequestProducer.requestUserInfo(deliveryManager.getUsername());
        // 3. 응답 받은 정보를 DTO에 담아 반환
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }
    @Transactional
    public void deleteManager(Long id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", id, JwtUserContext.getUsernameFromHeader());
        deliveryManager.softDelete(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
    }
/*    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponseDto> getAllManagers(String keyword, Long hubId, int page, int size, String sortBy, String status, boolean isAsc, boolean isDeleted) {
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
    }*/
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

    @Transactional
    public DeliveryManagerResponseDto updateManager(DeliveryManagerRequestDto requestDto) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(requestDto.getDeliveryManagerId())
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        deliveryManager.update(requestDto);
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", requestDto.getDeliveryManagerId(), JwtUserContext.getUsernameFromHeader());
        deliveryManager.setModified(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
        UserInfoDto userInfoDto = userRequestProducer.requestUserInfo(deliveryManager.getUsername());
        return DeliveryManagerResponseDto.of(savedDeliveryManager,userInfoDto);
    }
}