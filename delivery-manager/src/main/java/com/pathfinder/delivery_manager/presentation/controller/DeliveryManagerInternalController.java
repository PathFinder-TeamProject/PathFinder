package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/delivery-managers")
@RequiredArgsConstructor
@Slf4j
public class DeliveryManagerInternalController {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    /**
     * 내부 서비스용 사용자 정보 조회 API
     * DeliveryManager 서비스에서 FeignClient로 호출
     * @param username 조회할 사용자명
     * @return 사용자 정보 DTO
     */

    @DeleteMapping("/{username}")
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
    /*@GetMapping("/{hubId}/count")
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