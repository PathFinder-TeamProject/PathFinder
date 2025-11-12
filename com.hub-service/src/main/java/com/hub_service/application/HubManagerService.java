package com.hub_service.application;

import com.hub_service.domain.model.Hub;
import com.hub_service.domain.repository.HubRepository;
import com.hub_service.infrastructure.client.DeliveryManagerClient;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubManagerService {

    private final HubRepository hubRepository;

    public HubResponseDto getManagerInfoByHubId(String hubId) {
        Hub hub = hubRepository.findByHubIdAndDeletedAtIsNull(UUID.fromString(hubId))
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        return toResponse(hub);
    }
    private HubResponseDto toResponse(Hub hub) {
        return HubResponseDto.builder()
                .hubId(hub.getHubId())
                .hubManagerUsername(hub.getManagerUsername())
                .hubName(hub.getHubName())
                .hubAddress(hub.getHubAddress())
                .build();
    }
}