package com.pathfinder.order.domain.repository;

import com.pathfinder.order.domain.entity.OrderEntity;
import com.pathfinder.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    OrderEntity save(OrderEntity orderEntity);
    Optional<OrderEntity> findById(UUID Id);
    Page<OrderEntity> findAll(Pageable pageable);
    List<OrderEntity> findByProductId(UUID ProductId);
    List<OrderEntity> findBySupplierId(UUID SupplierId);
    List<OrderEntity> findByReceiverId(UUID ReceiverId);
    Optional<OrderEntity> findByDeliveryId(UUID DeliveryId);
    void deleteById(UUID Id);
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
}
