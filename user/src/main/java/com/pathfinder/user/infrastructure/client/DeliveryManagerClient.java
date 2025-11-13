package com.pathfinder.user.infrastructure.client;

import com.pathfinder.user.infrastructure.config.UserFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        name = "delivery-manager-service",
        path = "/internal",
        configuration = UserFeignClientConfig.class)
public interface DeliveryManagerClient {
    @DeleteMapping("/user/{username}")
    void deleteDeliveryManager(@PathVariable("username") String username);
}