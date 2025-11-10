package com.pathfinder.order.application;

import com.pathfinder.order.application.dto.request.OrderCreateRequestDto;
import com.pathfinder.order.application.dto.request.OrderUpdateRequestDto;
import com.pathfinder.order.application.dto.response.OrderResponseDto;
import com.pathfinder.order.application.exception.BusinessException;
import com.pathfinder.order.domain.entity.OrderEntity;
import com.pathfinder.order.domain.enums.OrderStatus;
import com.pathfinder.order.domain.repository.OrderRepository;
import com.pathfinder.order.presentation.enums.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceV1 {

    private final OrderRepository orderRepository;

    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto requestDto) {
        OrderEntity order = OrderEntity.builder()
                .productId(requestDto.getProductId())
                .supplierId(requestDto.getSupplierId())
                .quantity(requestDto.getQuantity())
                .receiverId(requestDto.getReceiverId())
                .request(requestDto.getRequest())
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .deadline(requestDto.getDeadline())
                .build();

        return new OrderResponseDto().fromEntity(orderRepository.save(order));
    }

    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ApiStatus.NOT_FOUND));

        if (order.getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new BusinessException(ApiStatus.CONFLICT);
        }

        order.changeStatus(OrderStatus.CANCELED);

        return new OrderResponseDto().fromEntity(order);
    }

    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto requestDto) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ApiStatus.NOT_FOUND));

        if (order.getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new BusinessException(ApiStatus.CONFLICT);
        }

        order.update(requestDto);

        return new OrderResponseDto().fromEntity(order);
    }

    @Cacheable(value = "orders", key = "#status != null ? #status.name() : 'all'")
    public Page<OrderResponseDto> getOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderEntity> orders;

        if (status == null) {
            orders = orderRepository.findAll(pageable);
        } else {
            orders = orderRepository.findByStatus(status, pageable);
        }

        return orders.map(order -> new OrderResponseDto().fromEntity(order));
    }

    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponseDto getOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ApiStatus.NOT_FOUND));

        if (order.getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new BusinessException(ApiStatus.CONFLICT);
        }

        return new OrderResponseDto().fromEntity(order);
    }
}
