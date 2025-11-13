package com.pathfinder.product.infrastructure.global.client;


import com.pathfinder.product.infrastructure.global.dto.CompanyDto;
import com.pathfinder.product.infrastructure.global.fallback.CompanyClientFallback;
import com.pathfinder.product.presentation.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "company-service",
        path = "/api/v1/companies",
        fallback = CompanyClientFallback.class
)
public interface CompanyClient {

    @GetMapping("/{companyId}")
    CompanyDto getCompanyById(@PathVariable UUID companyId);
}


