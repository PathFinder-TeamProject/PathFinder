package com.hub_service.domain.repository;

import com.hub_service.domain.model.HubRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRouteRepositoryCustom {
    Page<HubRoute> searchRoutes(String originName, String destinationName, Double minDistance, Double maxDistance, Pageable pageable);
}
