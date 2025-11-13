package com.pathfinder.product.infrastructure.global.fallback;

import com.pathfinder.product.infrastructure.global.client.HubClient;
import com.pathfinder.product.infrastructure.global.dto.HubDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class HubClientFallback implements HubClient {

    @Override
    public ResponseEntity<HubDto> getHub(UUID hubId) {
        log.error("[Fallback] 허브 호출 오류 hubId={}", hubId);
        return null;
    }
}
