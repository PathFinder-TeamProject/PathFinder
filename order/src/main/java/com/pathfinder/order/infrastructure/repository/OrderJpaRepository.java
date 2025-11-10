package com.pathfinder.order.infrastructure.repository;

import com.pathfinder.order.domain.entity.OrderEntity;
import com.pathfinder.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByProductId(UUID productId);

    List<OrderEntity> findBySupplierId(UUID supplierId);

    List<OrderEntity> findByReceiverId(UUID receiverId);

    Optional<OrderEntity> findByDeliveryId(UUID deliveryId);

    Page<OrderEntity> findByOrderStatus(Pageable pageable, OrderStatus orderStatus);
}
