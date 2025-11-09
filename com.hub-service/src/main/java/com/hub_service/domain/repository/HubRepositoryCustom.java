package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRepositoryCustom {
    Page<Hub> searchHubs(String keyword, String sortBy, Pageable pageable);
}
