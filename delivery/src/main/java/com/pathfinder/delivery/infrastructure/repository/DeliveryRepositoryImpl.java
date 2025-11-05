package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository jpaRepository;

    @Override
    public DeliveryEntity save(DeliveryEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<DeliveryEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DeliveryEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<DeliveryEntity> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

