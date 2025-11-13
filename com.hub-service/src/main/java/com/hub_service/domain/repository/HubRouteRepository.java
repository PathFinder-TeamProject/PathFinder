package com.hub_service.domain.repository;

import com.hub_service.domain.model.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface HubRouteRepository extends JpaRepository<HubRoute, UUID>, HubRouteRepositoryCustom {

    List<HubRoute> findByOriginHubIdAndDeletedAtIsNull(UUID originHubId);
    List<HubRoute> findByDestinationHubIdAndDeletedAtIsNull(UUID destinationHubId);
    List<HubRoute> findByDeletedAtIsNull();

}
