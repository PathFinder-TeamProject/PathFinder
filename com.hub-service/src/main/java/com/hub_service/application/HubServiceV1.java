package com.hub_service.application;

import com.hub_service.domain.model.Hub;
import com.hub_service.domain.repository.HubRepository;
import com.hub_service.presentation.dto.request.HubRequestDto;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubServiceV1 {

    private final HubRepository hubRepository;

    public List<HubResponseDto> getAllHubs() {
        return hubRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HubResponseDto getHubById(UUID id) {
        Hub hub = hubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        return toResponse(hub);
    }


    public HubResponseDto createHub(HubRequestDto requestDto) {
        Hub hub = Hub.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();
        Hub saved = hubRepository.save(hub);
        return toResponse(saved);
    }

    public HubResponseDto updateHub(UUID id, HubRequestDto requestDto) {
        Hub hub = hubRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        Hub updated = Hub.builder()
                .id(hub.getId())
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();
        return toResponse(hubRepository.save(updated));
    }

    public void deleteHub(UUID id) {
        if(!hubRepository.existsById(id)){
            throw new IllegalArgumentException("허브를 찾을 수 없습니다.");
        }
        hubRepository.deleteById(id);
    }

    private HubResponseDto toResponse(Hub hub) {
        return HubResponseDto.builder()
                .id(hub.getId())
                .name(hub.getName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .build();
    }
}
