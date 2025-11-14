package com.pathfinder.delivery_manager.application;
import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.delivery_manager.domain.repository.DeliveryManagerRepository;
import com.pathfinder.delivery_manager.infrastructure.client.DeliveryServiceClient;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerInternalServiceV1 {
    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional(readOnly = true)
    public DeliveryManagerResponseDto getManagerById(UUID deliveryManagerId) {
        DeliveryManagerEntity deliveryManager = deliveryManagerRepository.findByDeliveryManagerId(deliveryManagerId)
                .orElseThrow(() -> new EntityNotFoundException("배송 담당자를 찾을 수 없습니다."));
        return DeliveryManagerResponseDto.toDto(deliveryManager);
    }


    @Transactional(readOnly = true)
    public List<DeliveryManagerResponseDto> getDeliveryManagerInfoByHubId(UUID hubId) {
        List<DeliveryManagerEntity> deliveryManagerList = deliveryManagerRepository.findByHubId(hubId);
        return deliveryManagerList.stream()
                .map(DeliveryManagerResponseDto::toDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<DeliveryManagerResponseDto> getDeliveryManagerInfoByHubIdAndType(UUID hubId, DeliveryManagerTypeEnum type) {
        List<DeliveryManagerEntity> deliveryManagerList = deliveryManagerRepository.findByHubIdAndType(hubId, type);
        return deliveryManagerList.stream()
                .map(DeliveryManagerResponseDto::toDto)
                .collect(Collectors.toList());
    }

}