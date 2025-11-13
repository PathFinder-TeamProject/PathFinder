package com.pathfinder.product.infrastructure.global.fallback;

import com.pathfinder.product.infrastructure.global.client.CompanyClient;
import com.pathfinder.product.infrastructure.global.dto.CompanyDto;
import com.pathfinder.product.presentation.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

