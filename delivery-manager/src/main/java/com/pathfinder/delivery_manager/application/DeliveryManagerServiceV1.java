package com.pathfinder.delivery_manager.application;

import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerUpdateRequestDto;
import com.pathfinder.delivery_manager.application.excpetion.DeliveryManagerErrorCode;
import com.pathfinder.delivery_manager.application.excpetion.DuplicateDeliveryManagerException;
import com.pathfinder.delivery_manager.application.excpetion.TooManyDeliveryManagersException;
import com.pathfinder.delivery_manager.application.excpetion.UnauthorizedDeliveryManagerException;
import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.repository.DeliveryManagerRepository;
import com.pathfinder.delivery_manager.infrastructure.cache.HubCacheRepository;
import com.pathfinder.delivery_manager.infrastructure.client.HubServiceClient;
import com.pathfinder.delivery_manager.infrastructure.client.UserServiceClient;
import com.pathfinder.delivery_manager.infrastructure.security.JwtUserContext;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.delivery_manager.presentation.dto.response.HubInfoDto;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubCacheRepository hubCacheRepository;
    private final UserServiceClient userServiceClient;
    private final HubServiceClient hubServiceClient;

    @Transactional
    public DeliveryManagerResponseDto createDeliveryManager(DeliveryManagerRequestDto dto) {
        // 허브 캐시 기반 존재 여부 확인
    /*    if (!hubCacheRepository.exists(dto.getHubId())) {
            throw new IllegalArgumentException("허브 서비스에서 존재하지 않는 허브입니다.");
        }*/
        if(hubServiceClient.getHubInfo(dto.getHubId()) == null) {
            throw new IllegalArgumentException("허브 조회에 실패했습니다.");
        }
        validateHubAccess(dto.getHubId());
        log.info("Inside createDeliveryManager method - DeliveryManager getUsername: {}", dto.getUsername());
        UserInfoDto userInfoDto = userServiceClient.getUserInfo(dto.getUsername());
        if (userInfoDto == null || !userInfoDto.getRole().equals("DELIVERY_MANAGER")) {
            throw new EntityNotFoundException("등록 가능한 사용자 정보가 아닙니다.");
        }
        log.info("Inside createDeliveryManager method - DeliveryManager ID: {}", dto);
        if (deliveryManagerRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.info("배송 담당자 이미 존재 - Username: {}", dto.getUsername());
            throw new DuplicateDeliveryManagerException(DeliveryManagerErrorCode.DUPLICATE_DELIVERY_MANAGER);
        }
        if(deliveryManagerRepository.countByHubId(dto.getHubId()) < 10) {
            dto.setDeliveryOrder(deliveryManagerRepository.countByHubId(dto.getHubId()) + 1);
        } else {
            throw new TooManyDeliveryManagersException(DeliveryManagerErrorCode.TOO_MANY_DELIVERY_MANAGERS);
        }

        DeliveryManagerEntity deliveryManager = DeliveryManagerEntity.create(dto);
        deliveryManager.setCreate(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity saved = deliveryManagerRepository.save(deliveryManager);

        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerById(UUID id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByDeliveryManagerId(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        if(!deliveryManager.getUsername().equals(JwtUserContext.getUsernameFromHeader())) {
            validateHubAccess(deliveryManager.getHubId());
        }
        validateHubAccess(deliveryManager.getHubId());
        UserInfoDto userInfoDto = userServiceClient.getUserInfo(deliveryManager.getUsername());
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerByUsername(String username) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        if(!username.equals(JwtUserContext.getUsernameFromHeader())) {
            validateHubAccess(deliveryManager.getHubId());
        }
        UserInfoDto userInfoDto = userServiceClient.getUserInfo(username);
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }

    @Transactional
    public void deleteManager(UUID id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByDeliveryManagerId(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));

        validateHubAccess(deliveryManager.getHubId());
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", id, JwtUserContext.getUsernameFromHeader());
        deliveryManager.softDelete(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
    }
    @Transactional
    public void deleteManagerByUsername(String username) {
        log.info("[배송담당자 서비스] 유저 서비스로부터 배송담당자 삭제 요청 - username: {}", username);

        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[배송담당자 서비스] 배송담당자를 찾을 수 없음 - username: {}", username);
                    return new EntityNotFoundException("배송 담당자를 찾을 수 없습니다: " + username);
                });
        validateHubAccess(deliveryManager.getHubId());

        log.info("[배송담당자 서비스] 배송담당자 삭제 수행 - ID: {}, username: {}, 삭제 시각: {}",
                deliveryManager.getDeliveryManagerId(), username, Instant.now());

        deliveryManager.softDelete(Instant.now(), "SYSTEM_USER_DELETE");
        deliveryManagerRepository.save(deliveryManager);

        log.info("[배송담당자 서비스] 배송담당자 삭제 완료 - username: {}", username);
    }
    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponseDto> getAllManagers(UUID hubId, int page, int size, String sortBy, boolean isAsc) {
        Page<DeliveryManagerEntity> deliveryManagerPage;

        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        if(!sortBy.equals("modifiedAt") || !sortBy.isEmpty() && !sortBy.isBlank()) {
            sortBy = "createdAt";
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page>0?page-1:page, size, sort);
        if (hubId != null) {
            validateHubAccess(hubId);
            deliveryManagerPage = deliveryManagerRepository.findByHubId(hubId, pageable);
        } else {
            if(JwtUserContext.isHubManager()) {
                UUID managerHubId = hubServiceClient.getHubInfo(hubId).getHubId();
                deliveryManagerPage = deliveryManagerRepository.findByHubId(managerHubId, pageable);
                return deliveryManagerPage.map(DeliveryManagerResponseDto::forList);
            }
            deliveryManagerPage = deliveryManagerRepository.findAll(pageable);
        }
        return deliveryManagerPage.map(DeliveryManagerResponseDto::forList);
    }

    @Transactional
    public DeliveryManagerResponseDto updateManager(UUID deliveryManagerId, DeliveryManagerUpdateRequestDto requestDto) {
        validateHubAccess(requestDto.getHubId());
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByDeliveryManagerId(deliveryManagerId)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        if (JwtUserContext.isMaster()) {
            if(deliveryManagerRepository.countByHubId(requestDto.getHubId()) >= 10) {
                throw new TooManyDeliveryManagersException(DeliveryManagerErrorCode.TOO_MANY_DELIVERY_MANAGERS);
            } else{
                deliveryManager.setDeliveryOrder(deliveryManagerRepository.maxDeliveryOrderByHubId(requestDto.getHubId()) + 1);
            }
                deliveryManager.update(requestDto);
        } else if (JwtUserContext.isHubManager()) {
            // 허브 관리자는 같은 허브 소속인 경우 hubId만 수정 가능
            validateHubAccess(deliveryManager.getHubId());
            if(requestDto.getType()!=null) {
                throw new UnauthorizedDeliveryManagerException(DeliveryManagerErrorCode.UNAUTHORIZED_USER);
            }
            if(deliveryManagerRepository.countByHubId(requestDto.getHubId()) >= 10) {
                throw new TooManyDeliveryManagersException(DeliveryManagerErrorCode.TOO_MANY_DELIVERY_MANAGERS);
            } else{
                deliveryManager.setDeliveryOrder(deliveryManagerRepository.maxDeliveryOrderByHubId(requestDto.getHubId()) + 1);
            }
            deliveryManager.update(requestDto);
        } else {
            throw new UnauthorizedDeliveryManagerException(DeliveryManagerErrorCode.UNAUTHORIZED_USER);
        }

        deliveryManager.update(requestDto);
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", deliveryManagerId, JwtUserContext.getUsernameFromHeader());
        deliveryManager.setModified(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
//        UserInfoDto userInfoDto = userRequestProducer.requestUserInfo(deliveryManager.getUsername());
        UserInfoDto userInfo = UserInfoDto.builder()
                .username(deliveryManager.getUsername())
                .build();
        return DeliveryManagerResponseDto.of(savedDeliveryManager,userInfo);
    }
    @Transactional
    public void deleteManagersByHubId(UUID hubId) {
        List<DeliveryManagerEntity> managers = deliveryManagerRepository.findByHubId(hubId);
        if (managers.isEmpty()) {
            throw new EntityNotFoundException("해당 허브에 소속된 배송담당자가 없습니다.");
        }

        String deletedBy = JwtUserContext.getUsernameFromHeader();
        Instant deletedAt = Instant.now();

        for (DeliveryManagerEntity manager : managers) {
            log.info("Soft deleting Delivery Manager ID: {}, 허브 ID: {}, 삭제 주체: {}",
                    manager.getDeliveryManagerId(), hubId, deletedBy);
            manager.softDelete(deletedAt, deletedBy);
        }

        deliveryManagerRepository.saveAll(managers);
    }

    private void validateHubAccess(UUID hubId) {
        String role = JwtUserContext.getRoleFromHeader();
        String username = JwtUserContext.getUsernameFromHeader();

        if ("HUB_MANAGER".equals(role)) {
            HubInfoDto hubInfo = hubServiceClient.getHubInfo(hubId);
            if (!hubInfo.getHubManagerUsername().equals(username)) {
                throw new AccessDeniedException("허브관리자는 자신의 허브 데이터만 접근 가능합니다.");
            }
        }
    }
}