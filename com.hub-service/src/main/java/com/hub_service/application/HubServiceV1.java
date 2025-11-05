package com.hub_service.application;

import com.hub_service.presentation.dto.response.HubResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HubServiceV1 {

    public List<HubResponseDto> getAllHubs() {
        return List.of(
                HubResponseDto.builder()
                        .hubId("1")
                        .hubName("대전 허브")
                        .hubAddress("대전광역시 대덕구 문평동 140")
                        .latitude(36.41)
                        .longitude(127.40)
                        .build()
        );
    }

}
