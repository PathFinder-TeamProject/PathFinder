package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.QDeliveryEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.repository.DeliveryQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryQueryRepositoryImpl implements DeliveryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DeliveryEntity> searchDeliveries(
        UUID hubId,
        DeliveryStatus status,
        UUID deliveryManagerId,
        Pageable pageable
    ) {
        QDeliveryEntity delivery = QDeliveryEntity.deliveryEntity;

        List<DeliveryEntity> content = queryFactory
            .selectFrom(delivery)
            .where(
                hubIdEq(hubId),
                statusEq(status),
                deliveryManagerIdEq(deliveryManagerId),
                delivery.deletedAt.isNull()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(delivery.createdAt.desc())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(delivery.count())
            .from(delivery)
            .where(
                hubIdEq(hubId),
                statusEq(status),
                deliveryManagerIdEq(deliveryManagerId),
                delivery.deletedAt.isNull()
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        if (hubId == null) {
            return null;
        }
        QDeliveryEntity delivery = QDeliveryEntity.deliveryEntity;
        return delivery.fromHubId.eq(hubId)
            .or(delivery.toHubId.eq(hubId));
    }

    private BooleanExpression statusEq(DeliveryStatus status) {
        return status != null ? QDeliveryEntity.deliveryEntity.status.eq(status) : null;
    }

    private BooleanExpression deliveryManagerIdEq(UUID deliveryManagerId) {
        return deliveryManagerId != null ? QDeliveryEntity.deliveryEntity.deliveryManagerId.eq(deliveryManagerId) : null;
    }
}

