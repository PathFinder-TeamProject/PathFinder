package com.pathfinder.delivery_manager.infrastructure.client;
import com.pathfinder.delivery_manager.infrastructure.config.FeignClientConfig;
import com.pathfinder.delivery_manager.presentation.dto.response.HubInfoDto;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "hub-service",
        path = "/internal/hubs",
        configuration = FeignClientConfig.class
)
public interface HubServiceClient {
    /**
     * 특정 허브 ID로 허브 정보 조회
     * @param hubId 허브 ID
     * @return 허브 정보 DTO
     */
    @GetMapping("/{hubId}/info")
    HubInfoDto getHubInfo(@PathVariable("hubId") UUID hubId);
}