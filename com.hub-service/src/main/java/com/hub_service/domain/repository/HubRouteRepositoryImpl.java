package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import com.hub_service.domain.model.HubRoute;
import com.hub_service.domain.model.QHub;
import com.hub_service.domain.model.QHubRoute;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HubRoute> searchRoutes(String originName, String destinationName, Double minDistance, Double maxDistance, Pageable pageable) {

        QHubRoute qHubRoute = QHubRoute.hubRoute;
        QHub qOrigin = QHub.hub;
        QHub qDest = QHub.hub;

        JPAQuery<HubRoute> query = queryFactory.selectFrom(qHubRoute)
                .leftJoin(qOrigin).on(qHubRoute.originHubId.eq(qOrigin.hubId))
                .leftJoin(qDest).on(qHubRoute.destinationHubId.eq(qDest.hubId))
                .where(qHubRoute.deletedAt.isNull());

        if(originName != null && !originName.isEmpty()) {
            query.where(qOrigin.hubName.containsIgnoreCase(originName));
        }
        if(destinationName != null && !destinationName.isEmpty()) {
            query.where(qDest.hubName.containsIgnoreCase(destinationName));
        }
        if(minDistance != null && maxDistance != null) {
            query.where(qHubRoute.distanceKm.goe(minDistance));
        }
        if (maxDistance != null) {
            query.where(qHubRoute.distanceKm.loe(maxDistance));
        }

        long total = query.fetchCount();
        List<HubRoute> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize())
                .orderBy(qHubRoute.createdAt.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

}
