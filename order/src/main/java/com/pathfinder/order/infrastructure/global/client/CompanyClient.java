package com.pathfinder.order.infrastructure.global.client;

import com.pathfinder.order.infrastructure.global.fallback.CompanyClientFallback;
import com.pathfinder.order.infrastructure.global.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "company-service",
        path = "/v1/companies",
        fallback = CompanyClientFallback.class
)
public interface CompanyClient {

    @GetMapping("/{companyId}")
    CompanyDto getCompanyById(@PathVariable UUID companyId);
}

