package com.pathfinder.gateway.client;

import com.pathfinder.gateway.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/v1/users")
public interface UserFeignClient {

    @GetMapping("/{username}")
    UserResponseDto getUserByUsername(@PathVariable("username") String username);
}