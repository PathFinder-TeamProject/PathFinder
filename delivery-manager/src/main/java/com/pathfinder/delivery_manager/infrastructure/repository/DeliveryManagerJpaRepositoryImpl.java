package com.pathfinder.delivery_manager.infrastructure.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DeliveryManagerJpaRepositoryImpl implements DeliveryManagerRepository {
    private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;
    @Override
    public Optional<DeliveryManagerEntity> findByUsername(String username) {
        return deliveryManagerJpaRepository.findByUsername(username);
    }

    @Override
    public int countByHubId(Long hubId) {
        return deliveryManagerJpaRepository.countByHubId(hubId);
    }

    @Override
    public Optional<DeliveryManagerEntity> findById(Long id) {
        return deliveryManagerJpaRepository.findById(id);
    }

    @Override
    public DeliveryManagerEntity save(DeliveryManagerEntity entity) {
        return deliveryManagerJpaRepository.save(entity);
    }

    @Override
    public Page<DeliveryManagerEntity> findByHubId(Long hubId, Pageable pageable) {
        return deliveryManagerJpaRepository.findByHubId(hubId, pageable);
    }

    @Override
    public Page<DeliveryManagerEntity> findAll(Pageable pageable) {
        return deliveryManagerJpaRepository.findAll(pageable);
    }
}
