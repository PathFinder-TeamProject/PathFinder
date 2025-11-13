package com.hub_service.presentation.controller;

import com.hub_service.application.HubManagerService;
import com.hub_service.infrastructure.client.DeliveryManagerClient;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/hubs")
@RequiredArgsConstructor
public class HubManagerController {

    private final HubManagerService hubManagerService;

    @GetMapping("/{hubId}/info")
    public HubResponseDto getHubManager(@PathVariable String hubId) {
        System.out.println("허브 매니저 정보 조회 요청 받은 허브 ID: " + hubId);
        return hubManagerService.getManagerInfoByHubId(hubId);
    }
}