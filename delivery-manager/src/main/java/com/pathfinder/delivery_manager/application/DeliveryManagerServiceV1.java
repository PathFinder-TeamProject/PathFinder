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
import com.pathfinder.delivery_manager.infrastructure.client.UserServiceClient;
import com.pathfinder.delivery_manager.infrastructure.security.JwtUserContext;
import com.pathfinder.delivery_manager.infrastructure.kafka.UserRequestProducer;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubCacheRepository hubCacheRepository;
    private final UserRequestProducer userRequestProducer;
    private final UserServiceClient userServiceClient;

    @Transactional
    public DeliveryManagerResponseDto createDeliveryManager(DeliveryManagerRequestDto dto) {
        // 허브 캐시 기반 존재 여부 확인
    /*    if (!hubCacheRepository.exists(dto.getHubId())) {
            throw new IllegalArgumentException("허브 서비스에서 존재하지 않는 허브입니다.");
        }*/
        if(JwtUserContext.getRoleFromHeader().equals("HUB_MANAGER")|| JwtUserContext.getRoleFromHeader().equals("ROLE_HUB_MANAGER")) {
           checkHubManager(JwtUserContext.getUsernameFromHeader(), dto.getHubId());
        }
        UserInfoDto userInfoDto = userServiceClient.getUserInfo(dto.getUsername());
        if(userInfoDto == null) {
            throw new EntityNotFoundException("사용자 서비스에서 해당 사용자를 찾을 수 없습니다.");
        }
        log.info("Inside createDeliveryManager method - DeliveryManager ID: {}", dto);
        if (deliveryManagerRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.info("배송 담당자 이미 존재 - Username: {}", dto.getUsername());
            throw new DuplicateDeliveryManagerException(DeliveryManagerErrorCode.DUPLICATE_DELIVERY_MANAGER);
        }
        if(dto.getType().equals("HUB_MANAGER")) {
            if(deliveryManagerRepository.countByHubId(0L) < 10) {
                dto.setDeliveryOrder(deliveryManagerRepository.countByHubId(0L) + 1);
            } else {
                throw new TooManyDeliveryManagersException(DeliveryManagerErrorCode.TOO_MANY_DELIVERY_MANAGERS);
            }
        } else {
            dto.setDeliveryOrder(deliveryManagerRepository.countByHubId(dto.getHubId()) + 1);
        }

        DeliveryManagerEntity deliveryManager = DeliveryManagerEntity.create(dto);
        deliveryManager.setCreate(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity saved = deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }

   @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerById(Long id) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
       // 2. Kafka Request-Reply를 사용하여 User Service에 사용자 정보 요청 및 응답 받기
       UserInfoDto userInfoDto = userServiceClient.getUserInfo(deliveryManager.getUsername());
       // 3. 응답 받은 정보를 DTO에 담아 반환
       return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }
    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerByUsername(String username) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        // 2. Kafka Request-Reply를 사용하여 User Service에 사용자 정보 요청 및 응답 받기
        UserInfoDto userInfoDto = userServiceClient.getUserInfo(username);
        // 3. 응답 받은 정보를 DTO에 담아 반환
        return DeliveryManagerResponseDto.of(deliveryManager, userInfoDto);
    }
    @Transactional
    public void deleteManager(Long id) {
        if(JwtUserContext.isHubManager()) {
            checkHubManager(JwtUserContext.getUsernameFromHeader(), id);
        }
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", id, JwtUserContext.getUsernameFromHeader());
        deliveryManager.softDelete(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
    }
    public Page<DeliveryManagerResponseDto> getAllManagers(Long hubId, int page, int size, String sortBy, boolean isAsc) {
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
            deliveryManagerPage = deliveryManagerRepository.findByHubId(hubId, pageable);
        } else {
            deliveryManagerPage = deliveryManagerRepository.findAll(pageable);
        }
        return deliveryManagerPage.map(DeliveryManagerResponseDto::forList);
    }

    @Transactional
    public DeliveryManagerResponseDto updateManager(Long deliveryManagerId, DeliveryManagerUpdateRequestDto requestDto) {
        if(JwtUserContext.getUsernameFromHeader().equals("HUB_MANAGER")) {
            checkHubManager(JwtUserContext.getUsernameFromHeader(), requestDto.getHubId());
        }
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        if (JwtUserContext.isMaster()) {
            deliveryManager.update(requestDto);

        } else if (JwtUserContext.isHubManager()) {
            // 허브 관리자는 같은 허브 소속인 경우 hubId만 수정 가능
            checkHubManager(JwtUserContext.getUsernameFromHeader(), deliveryManager.getHubId());
            if(requestDto.getType()!=null) {
                throw new UnauthorizedDeliveryManagerException(DeliveryManagerErrorCode.UNAUTHORIZED_USER);
            }
            deliveryManager.update(requestDto);
        } else {
            throw new UnauthorizedDeliveryManagerException(DeliveryManagerErrorCode.UNAUTHORIZED_USER);
        }

        deliveryManager.update(requestDto);
        log.info("Soft deleting Delivery Manager ID:{}, 삭제하는 주체:{}", deliveryManagerId, JwtUserContext.getUsernameFromHeader());
        deliveryManager.setModified(Instant.now(), JwtUserContext.getUsernameFromHeader());
        DeliveryManagerEntity savedDeliveryManager = deliveryManagerRepository.save(deliveryManager);
        UserInfoDto userInfoDto = userRequestProducer.requestUserInfo(deliveryManager.getUsername());
        return DeliveryManagerResponseDto.of(savedDeliveryManager,userInfoDto);
    }
    @Transactional
    public void deleteManagersByHubId(Long hubId) {
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

    public void checkHubManager(String username, Long hubId) {
        Long userHubId = 0L;
//        hubId = hubCacheRepository.getHubIdByManagerUsername(username);
        if(JwtUserContext.getRoleFromHeader().equals("HUB_MANAGER") && userHubId.equals(hubId)) {
            throw new IllegalArgumentException("허브 매니저는 자신의 허브에 속한 배송 담당자만 관리할 수 있습니다.");
        }
    }
}