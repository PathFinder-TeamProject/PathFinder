package com.hub_service.application;

import com.hub_service.domain.model.HubRoute;
import com.hub_service.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteSearchService {

    private final HubRouteRepository hubRouteRepository;

    public Page<HubRoute> search(String originName, String destName, Double minDistance, Double maxDistance, Pageable pageable) {
        return hubRouteRepository.searchRoutes(originName, destName, minDistance, maxDistance, pageable);
    }
}
