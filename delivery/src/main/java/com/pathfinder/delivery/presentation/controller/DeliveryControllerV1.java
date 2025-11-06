package com.pathfinder.delivery.presentation.controller;

import com.pathfinder.delivery.presentation.dto.request.CreateDeliveryRequest;
import com.pathfinder.delivery.presentation.dto.request.UpdateDeliveryRequest;
import com.pathfinder.delivery.presentation.dto.response.DeliveryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/deliveries")
public class DeliveryControllerV1 {

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody CreateDeliveryRequest request) {
        DeliveryResponse response = DeliveryResponse.builder()
                .id(1L)
                .orderId(request.getOrderId())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .address(request.getAddress())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponse> updateDelivery(
            @PathVariable Long id,
            @RequestBody UpdateDeliveryRequest request) {
        
        DeliveryResponse response = DeliveryResponse.builder()
                .id(id)
                .orderId(1001L)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .address(request.getAddress())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable Long id) {
        DeliveryResponse response = DeliveryResponse.builder()
                .id(id)
                .orderId(1001L)
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .address("서울시 강남구 테헤란로 123")
                .status("IN_TRANSIT")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        List<DeliveryResponse> responses = new ArrayList<>();
        
        responses.add(DeliveryResponse.builder()
                .id(1L)
                .orderId(1001L)
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .address("서울시 강남구 테헤란로 123")
                .status("IN_TRANSIT")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build());

        responses.add(DeliveryResponse.builder()
                .id(2L)
                .orderId(1002L)
                .recipientName("김철수")
                .recipientPhone("010-9876-5432")
                .address("서울시 서초구 서초대로 456")
                .status("DELIVERED")
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build());

        responses.add(DeliveryResponse.builder()
                .id(3L)
                .orderId(1003L)
                .recipientName("이영희")
                .recipientPhone("010-5555-6666")
                .address("부산시 해운대구 해운대로 789")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryResponse response = DeliveryResponse.builder()
                .id(1L)
                .orderId(orderId)
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .address("서울시 강남구 테헤란로 123")
                .status("IN_TRANSIT")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}

