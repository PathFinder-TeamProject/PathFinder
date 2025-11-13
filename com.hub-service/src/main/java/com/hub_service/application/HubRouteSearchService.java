package com.hub_service.application;

import com.hub_service.domain.model.HubRoute;
import com.hub_service.domain.repository.HubRouteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteSearchService {

    private final HubRouteRepository hubRouteRepository;

    @Operation(summary = "허브 담당자 조회", description = "허브 ID를 기준으로 허브 관리자(Manager) 정보를 조회합니다.")
    @Parameter(name = "hubId", description = "허브의 고유 ID", example = "b8b37a03-9c25-4b8c-9f73-7dcf945e0c45")
    public Page<HubRoute> search(String originName, String destName, Double minDistance, Double maxDistance, Pageable pageable) {
        return hubRouteRepository.searchRoutes(originName, destName, minDistance, maxDistance, pageable);
    }
}
