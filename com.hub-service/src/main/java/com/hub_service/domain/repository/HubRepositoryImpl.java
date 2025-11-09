package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import com.hub_service.domain.model.QHub;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Hub> searchHubs(String keyword, String sortBy, Pageable pageable) {
        QHub hub = QHub.hub;

        BooleanExpression condition = hub.deletedAt.isNull();

        if (keyword != null && !keyword.isEmpty()) {
            condition = condition.and(
                    hub.hubName.containsIgnoreCase(keyword)
                            .or(hub.hubAddress.containsIgnoreCase(keyword))
            );
        }
        OrderSpecifier<?> orderSpecifier = switch (sortBy) {
            case "createdAt" -> hub.createdAt.desc();
            case "updatedAt" -> hub.updatedAt.desc();
            default -> hub.hubName.asc();
        };

        List<Hub> result = queryFactory
                .selectFrom(hub)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(hub.count())
                .from(hub)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(result, pageable, total);

    }

}
