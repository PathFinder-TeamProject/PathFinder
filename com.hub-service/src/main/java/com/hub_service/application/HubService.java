package com.hub_service.application;

import com.hub_service.domain.model.Hub;
import com.hub_service.domain.repository.HubRepository;
import com.hub_service.infrastructure.client.DeliveryManagerClient;
import com.hub_service.presentation.dto.request.HubRequestDto;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {

    private final HubRepository hubRepository;
    private final DeliveryManagerClient deliveryManagerClient;

    @Cacheable(value = "hubs", key = "'all'")
    public List<HubResponseDto> getAllHubs() {
        return hubRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "hubs", key = "#hubId")
    public HubResponseDto getHubById(UUID hubId) {
        Hub hub = hubRepository.findByHubIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        return toResponse(hub);
    }

    @Transactional
    public HubResponseDto createHub(HubRequestDto requestDto) {
        Hub hub = Hub.builder()
                .hubName(requestDto.getHubName())
                .hubAddress(requestDto.getHubAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();
        return toResponse(hubRepository.save(hub));
    }

    @CacheEvict(value = "hubs", allEntries = true)
    @Transactional
    public HubResponseDto updateHub(UUID hubId, HubRequestDto requestDto) {
        Hub hub = hubRepository.findByHubIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        hub.update(requestDto.getHubName(), requestDto.getHubAddress(),
                requestDto.getLatitude(), requestDto.getLongitude());
        return toResponse(hubRepository.save(hub));
    }

    @CacheEvict(value = "hubs", allEntries = true)
    @Transactional
    public void deleteHub(UUID hubId, String username) {
        Hub hub = hubRepository.findByHubIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브가 존재하지 않거나 이미 삭제되었습니다."));
        hub.softDelete(username);
        hubRepository.save(hub);
        //배송담당자 서비스에 허브삭제 알림 발송
        deliveryManagerClient.notifyHubDeletion(hubId.toString());
    }

    @Transactional
    public Page<HubResponseDto> searchHubs(String keyword, String sortBy, Pageable pageable) {
        return hubRepository.searchHubs(keyword, sortBy, pageable)
                .map(this::toResponse);
    }

    private HubResponseDto toResponse(Hub hub) {
        return HubResponseDto.builder()
                .hubId(hub.getHubId())
                .hubName(hub.getHubName())
                .hubAddress(hub.getHubAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .build();
    }
}

