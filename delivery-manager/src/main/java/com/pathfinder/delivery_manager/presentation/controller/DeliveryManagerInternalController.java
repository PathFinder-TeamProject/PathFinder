package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerInternalServiceV1;
import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Slf4j
public class DeliveryManagerInternalController {

    private final DeliveryManagerServiceV1 deliveryManagerService;
    private final DeliveryManagerInternalServiceV1 deliveryManagerInternalService;

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Void> deleteDeliveryManagerByUsername(@PathVariable String username) {
        try {
            deliveryManagerService.deleteManagerByUsername(username);
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

        try {
            deliveryManagerService.deleteManagersByHubId(UUID.fromString(hubId));
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("[배송담당자 컨트롤러] 허브 삭제 처리 실패 - hubId: {}, error: {}",
                    hubId, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/deliverys/{deliveryManagerId}")
    public ApiResponse<DeliveryManagerResponseDto> getDeliveryManagerInfo(@PathVariable UUID deliveryManagerId) {

        try {
            DeliveryManagerResponseDto deliveryManagerInfo = deliveryManagerInternalService.getManagerById(deliveryManagerId);
            return ApiResponse.success(deliveryManagerInfo);

        } catch (Exception e) {
            return ApiResponse.fail("배송담당자 조회 실패 : ", e.getMessage());
        }
    }
    @GetMapping("/deliverys/hub/{hubId}")
    public ApiResponse<List<DeliveryManagerResponseDto>> getDeliveryManagerInfoByHubId(@PathVariable UUID hubId) {

        try {
            List<DeliveryManagerResponseDto> deliveryManagerInfoList = deliveryManagerInternalService.getDeliveryManagerInfoByHubId(hubId);
            return ApiResponse.success(deliveryManagerInfoList);

        } catch (Exception e) {
            return ApiResponse.fail("배송담당자 조회 실패 : ", e.getMessage());
        }
    }
    @GetMapping("/deliverys/hub/{hubId}/type/{type}")
    public ApiResponse<List<DeliveryManagerResponseDto>> getDeliveryManagerInfoByHubIdAndType(@PathVariable UUID hubId, @PathVariable String type) {

        try {
            List<DeliveryManagerResponseDto> deliveryManagerInfoList = deliveryManagerInternalService.getDeliveryManagerInfoByHubIdAndType(hubId, DeliveryManagerTypeEnum.valueOf(type));
            return ApiResponse.success(deliveryManagerInfoList);

        } catch (Exception e) {
            return ApiResponse.fail("배송담당자 조회 실패 : ", e.getMessage());
        }
    }
}