package com.pathfinder.order.application;

import com.pathfinder.global.presentation.exception.PathException;
import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.order.application.dto.request.OrderCreateRequestDto;
import com.pathfinder.order.application.dto.request.OrderUpdateRequestDto;
import com.pathfinder.order.application.dto.response.OrderResponseDto;
import com.pathfinder.order.application.exception.BusinessException;
import com.pathfinder.order.domain.entity.OrderEntity;
import com.pathfinder.order.domain.enums.OrderStatus;
import com.pathfinder.order.domain.repository.OrderRepository;
import com.pathfinder.order.infrastructure.global.client.CompanyClient;
import com.pathfinder.order.infrastructure.global.client.ProductClient;
import com.pathfinder.order.infrastructure.global.dto.CompanyDto;
import com.pathfinder.order.infrastructure.global.dto.OrderCreatedEvent;
import com.pathfinder.order.infrastructure.global.dto.ProductDto;
import com.pathfinder.order.infrastructure.global.security.jwt.JwtUserContext;
import com.pathfinder.order.presentation.enums.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceV1 {

    private final OrderRepository orderRepository;
    private final CompanyClient companyClient;
    private final ProductClient productClient;

    @CacheEvict(value = "orders", allEntries = true)
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto requestDto) {
        validateCompany(requestDto.getSupplierId());
        validateCompany(requestDto.getReceiverId());

        int stock = validateProduct(requestDto.getProductId());

        if (stock == 0 || stock < requestDto.getQuantity()) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        ApiResponse<Void> response = productClient.decreaseStock(requestDto.getProductId(), requestDto.getQuantity());

        if (!"200".equals(response.getCode())) {
            throw new IllegalStateException("상품 재고 차감 실패: " + response.getMessage());
        }


        OrderEntity order = OrderEntity.builder()
                .productId(requestDto.getProductId())
                .supplierId(requestDto.getSupplierId())
                .quantity(requestDto.getQuantity())
                .receiverId(requestDto.getReceiverId())
                .request(requestDto.getRequest())
                .orderStatus(OrderStatus.CREATED)
                .deadline(requestDto.getDeadline())
                .build();

        order.setCreate(Instant.now(), JwtUserContext.getUsernameFromHeader());


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
        order.cancel(JwtUserContext.getUsernameFromHeader());

        ApiResponse<Void> response = productClient.decreaseStock(order.getProductId(), order.getQuantity() * -1);

        if (!"200".equals(response.getCode())) {
            throw new IllegalStateException("상품 재고 차감 실패: " + response.getMessage());
        }

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

        int stock = validateProduct(order.getProductId());

        if(stock == 0 || stock < requestDto.getQuantity()) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        ApiResponse<Void> response = productClient.decreaseStock(order.getProductId(), requestDto.getQuantity() - order.getQuantity());

        if (!"200".equals(response.getCode())) {
            throw new IllegalStateException("상품 재고 차감 실패: " + response.getMessage());
        }

        order.update(requestDto);
        order.setModified(Instant.now(), JwtUserContext.getUsernameFromHeader());

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

    @Transactional
    public void setDelivery(UUID orderId, UUID deliveryId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ApiStatus.NOT_FOUND));

        if(!order.getOrderStatus().equals(OrderStatus.CREATED)) {
            throw new RuntimeException("이미 배송중인 주문입니다.");
        }

        order.changeStatus(OrderStatus.IN_PROGRESS);
        order.setDelivery(deliveryId);
    }

        public void validateCompany(UUID companyId) {
        CompanyDto company = companyClient.getCompanyById(companyId);

        if (company == null) {
            throw new IllegalArgumentException("존재하지 않는 회사 ID입니다: " + companyId);
        }
    }

    public Integer validateProduct(UUID productId) {
        ApiResponse<ProductDto> response = productClient.getProductById(productId);

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("상품 정보를 불러올 수 없습니다.");
        }

        return response.getData().getStock();
    }
}
