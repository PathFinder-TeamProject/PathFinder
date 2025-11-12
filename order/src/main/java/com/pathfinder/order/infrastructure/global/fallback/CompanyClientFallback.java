package com.pathfinder.order.infrastructure.global.fallback;

import com.pathfinder.order.infrastructure.global.client.CompanyClient;
import com.pathfinder.order.infrastructure.global.dto.CompanyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CompanyClientFallback implements CompanyClient {

    @Override
    public CompanyDto getCompanyById(UUID companyId) {
        log.error("[Fallback] 회사 서비스 호출 실패 - companyId: {}", companyId);

        return null;
    }
}

