package com.pathfinder.user.presentation.controller;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.jwt.JwtUserContext;
import com.pathfinder.user.presentation.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "유저 API", description = "회원가입, 로그인, 사용자 정보 조회 및 승인 관리 등 사용자 관련 기능을 제공합니다.")
public class UserControllerV1 {
    private final UserServiceV1 userServiceV1;

    @PostMapping("/auth/register")
    public ApiResponse<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        SignupResponseDto response = userServiceV1.signup(requestDto);
        return ApiResponse.success(response);
    }

    @GetMapping("/users")
    public ApiResponse<Page<UserResponseDto>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        Page<UserResponseDto> response = userServiceV1.getUserList(page - 1, size, sortBy, isAsc);
        return ApiResponse.success(response);
    }

    @PostMapping("/users/{username}/confirm-member")
    public ApiResponse<UserStatusUpdateResponseDto> updateUserConfirm(
            @PathVariable String username,
            @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        userServiceV1.checkApproved(JwtUserContext.getUsernameFromHeader());
        UserStatusUpdateResponseDto response = userServiceV1.updateUserStatus(
                username, userStatusUpdateRequestDto);
        return ApiResponse.success(response);
    }

    @PatchMapping("/users/{username}/role")
    public ApiResponse<UserRoleUpdateResponseDto> updateUserRole(
            @PathVariable String username,
            @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userServiceV1.checkApproved(userDetails.getUsername());
        UserRoleUpdateResponseDto response = userServiceV1.userRoleUpdate(
                username, userRoleUpdateRequestDto, userDetails.getUser());
        return ApiResponse.success(response);
    }

    @GetMapping("/users/myInfo")
    public ApiResponse<UserResponseDto> getMyUserInfo() {

        UserResponseDto response = userServiceV1.getUser(JwtUserContext.getUsernameFromHeader());
        return ApiResponse.success(response);
    }

    @GetMapping("/users/{username}")
    public ApiResponse<UserResponseDto> getUserInfo(
            @PathVariable String username) {
        UserResponseDto response = userServiceV1.getUser(username);
        return ApiResponse.success(response);
    }

    @PutMapping("/users/{username}")
    public ApiResponse<UserUpdateResponseDto> updateUserInfo(
            @PathVariable String username,
            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {

        UserUpdateResponseDto response = userServiceV1.updateUser(
                username, userUpdateRequestDto, JwtUserContext.getUsernameFromHeader());

        return ApiResponse.success(response);
    }

    @DeleteMapping("/users/{username}")
    public ApiResponse<UserDeleteResponseDto> deleteUser(
            @PathVariable String username) {

        userServiceV1.deleteUser(username, JwtUserContext.getUsernameFromHeader());
        return ApiResponse.noContent();
    }

}
