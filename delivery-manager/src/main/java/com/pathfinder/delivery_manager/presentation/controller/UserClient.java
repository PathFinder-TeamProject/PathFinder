package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/v1/users")
public interface UserClient {
    @GetMapping("/{userId}")
    UserInfoDto getUserById(@PathVariable("userId") String userId);
}