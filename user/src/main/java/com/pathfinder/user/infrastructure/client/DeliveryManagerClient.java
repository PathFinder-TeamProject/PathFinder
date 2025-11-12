package com.pathfinder.user.infrastructure.client;

import com.pathfinder.user.infrastructure.config.UserFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        name = "delivery-manager-service",
        path = "/internal/delivery-managers",
        configuration = UserFeignClientConfig.class)
public interface DeliveryManagerClient {
    /**
     * 배송담당자 생성
     *
     * @param request 배송담당자 생성 요청 DTO
     */
    /*
    @PostMapping
    void createDeliveryManager(@RequestBody DeliveryManagerRequestDto request);
    */

    /**
     * 배송담당자 삭제
     * 유저가 삭제될 때 해당 배송담당자 정보도 삭제
     * @param username 삭제할 배송담당자의 사용자명
     */
    @DeleteMapping("/{username}")
    void deleteDeliveryManager(@PathVariable("username") String username);
}