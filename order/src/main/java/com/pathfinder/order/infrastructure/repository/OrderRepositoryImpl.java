package com.pathfinder.order.infrastructure.repository;

import com.pathfinder.order.domain.entity.OrderEntity;
import com.pathfinder.order.domain.enums.OrderStatus;
import com.pathfinder.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        return orderJpaRepository.save(orderEntity);
    }

    @Override
    public Optional<OrderEntity> findById(UUID Id) {
        return orderJpaRepository.findById(Id);
    }

    @Override
    public Page<OrderEntity> findAll(Pageable pageable) {
        return orderJpaRepository.findAll(pageable);
    }

    @Override
    public List<OrderEntity> findByProductId(UUID ProductId) {
        return orderJpaRepository.findByProductId(ProductId);
    }

    @Override
    public List<OrderEntity> findBySupplierId(UUID SupplierId) {
        return orderJpaRepository.findBySupplierId(SupplierId);
    }

    @Override
    public List<OrderEntity> findByReceiverId(UUID ReceiverId) {
        return orderJpaRepository.findByReceiverId(ReceiverId);
    }

    @Override
    public Optional<OrderEntity> findByDeliveryId(UUID DeliveryId) {
        return orderJpaRepository.findByDeliveryId(DeliveryId);
    }

    @Override
    public void deleteById(UUID Id) {
        orderJpaRepository.deleteById(Id);
    }

    @Override
    public Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable) {
        return orderJpaRepository.findByOrderStatus(pageable, status);
    }
}
