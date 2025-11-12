package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Slf4j
public class DeliveryManagerInternalController {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    @DeleteMapping("/delivery-managers/{username}")
    public ResponseEntity<Void> deleteDeliveryManagerByUsername(@PathVariable String username) {
        log.info("[배송담당자 컨트롤러] 배송담당자 삭제 요청 - username: {}", username);

        try {
            deliveryManagerService.deleteManagerByUsername(username);
            log.info("[배송담당자 컨트롤러] 배송담당자 삭제 완료 - username: {}", username);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("[배송담당자 컨트롤러] 배송담당자 삭제 실패 - username: {}, error: {}",
                    username, e.getMessage());
            throw e;
        }
    }
    /**
     * 허브 삭제 알림 수신 - Hub 서비스에서 호출
     * @param hubId 삭제된 허브 ID
     * @return 응답 엔티티
     */
    @DeleteMapping("/hubs/{hubId}")
    public ResponseEntity<Void> handleHubDeletion(@PathVariable String hubId) {
        log.info("[배송담당자 컨트롤러] 허브 삭제 알림 수신 - hubId: {}", hubId);

        try {
            deliveryManagerService.deleteManagersByHubId(UUID.fromString(hubId));
            log.info("[배송담당자 컨트롤러] 허브 삭제 처리 완료 - hubId: {}", hubId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("[배송담당자 컨트롤러] 허브 삭제 처리 실패 - hubId: {}, error: {}",
                    hubId, e.getMessage());
            throw e;
        }
    }
    /*@GetMapping("/delivery-managers/{username}/exists")
    @GetMapping("/{hubId}/count")
    public ResponseEntity<Long> countDeliveryManagersByHubId(@PathVariable Long hubId) {
        log.info("[배송담당자 컨트롤러] 허브 ID로 배송담당자 수 조회 요청 - hubId: {}", hubId);
        try {
            Long count = deliveryManagerService.countManagersByHubId(hubId);
            log.info("[배송담당자 컨트롤러] 허브 ID로 배송담당자 수 조회 완료 - hubId: {}, count: {}", hubId, count);
            return ResponseEntity.ok(count);

        } catch (Exception e) {
            log.error("[배송담당자 컨트롤러] 허브 ID로 배송담당자 수 조회 실패 - hubId: {}, error: {}",
                    hubId, e.getMessage());
            throw e;
        }
    }*/
}