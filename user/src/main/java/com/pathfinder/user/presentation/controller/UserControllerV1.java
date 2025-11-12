package com.pathfinder.user.presentation.controller;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.exception.UserErrorCode;
import com.pathfinder.user.application.exception.ValidationException;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.enums.UserRoleEnum;
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
public class UserControllerV1 {
    @Value("${server.port}") // 애플리케이션이 실행 중인 포트를 주입받습니다.
    private String serverPort;

    private final UserServiceV1 userServiceV1;

    @PostMapping("/auth/register")
    public ApiResponse<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        log.info("POST /auth/register - 회원가입 요청 - username: {}, email: {}, role: {}",
                requestDto.getUsername(), requestDto.getEmail(), requestDto.getRole());
        SignupResponseDto response = userServiceV1.signup(requestDto);
        log.info("POST /auth/register - 회원가입 완료 - username: {}", response.getUsername());
        return ApiResponse.success(response);
    }

    @GetMapping("/users")
    public ApiResponse<Page<UserResponseDto>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        log.info("GET /users - 유저 목록 조회 - page: {}, size: {}, sortBy: {}, isAsc: {}",
                page, size, sortBy, isAsc);

        Page<UserResponseDto> response = userServiceV1.getUserList(page - 1, size, sortBy, isAsc);
        log.info("GET /users - 유저 목록 조회 완료 - 총 {}건, 현재 페이지 {}건",
                response.getTotalElements(), response.getContent().size());

        return ApiResponse.success(response);
    }

    @PostMapping("/users/{username}/confirm-member")
    public ApiResponse<UserStatusUpdateResponseDto> updateUserConfirm(
            @PathVariable String username,
            @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto) {

        log.info("POST /users/{}/confirm-member - 유저 상태 변경 요청 - 변경할 상태: {}, 요청자: {}",
                username, userStatusUpdateRequestDto.getStatus(), JwtUserContext.getUsernameFromHeader());

        userServiceV1.checkApproved(JwtUserContext.getUsernameFromHeader());
        UserStatusUpdateResponseDto response = userServiceV1.updateUserStatus(
                username, userStatusUpdateRequestDto);

        log.info("POST /users/{}/confirm-member - 유저 상태 변경 완료 - 새 상태: {}",
                username, response.getStatus());

        return ApiResponse.success(response);
    }

    @PatchMapping("/users/{username}/role")
    public ApiResponse<UserRoleUpdateResponseDto> updateUserRole(
            @PathVariable String username,
            @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("PATCH /users/{}/role - 유저 권한 변경 요청 - 변경할 권한: {}, 요청자: {}",
                username, userRoleUpdateRequestDto.getRole(), userDetails.getUsername());

        userServiceV1.checkApproved(userDetails.getUsername());
        UserRoleUpdateResponseDto response = userServiceV1.userRoleUpdate(
                username, userRoleUpdateRequestDto, userDetails.getUser());

        log.info("PATCH /users/{}/role - 유저 권한 변경 완료 - 새 권한: {}",
                username, response.getRole());

        return ApiResponse.success(response);
    }

    @GetMapping("/users/myInfo")
    public ApiResponse<UserResponseDto> getMyUserInfo() {

        log.info("GET /users/myInfo - 내 정보 조회 - username: {}", JwtUserContext.getUsernameFromHeader());

        UserResponseDto response = userServiceV1.getUser(JwtUserContext.getUsernameFromHeader());

        log.info("GET /users/myInfo - 내 정보 조회 완료 - username: {}", response.getUsername());

        return ApiResponse.success(response);
    }

    @GetMapping("/users/{username}")
    public ApiResponse<UserResponseDto> getUserInfo(
            @PathVariable String username) {

        log.info("GET /users/{} - 유저 정보 조회 - 요청자: {}", username, JwtUserContext.getUsernameFromHeader());

        UserResponseDto response = userServiceV1.getUser(username);

        log.info("GET /users/{} - 유저 정보 조회 완료", username);

        return ApiResponse.success(response);
    }

    @PutMapping("/users/{username}")
    public ApiResponse<UserUpdateResponseDto> updateUserInfo(
            @PathVariable String username,
            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {

        UserUpdateResponseDto response = userServiceV1.updateUser(
                username, userUpdateRequestDto, JwtUserContext.getUsernameFromHeader());

        log.info("PUT /users/{} - 유저 정보 수정 완료", username);

        return ApiResponse.success(response);
    }

    @DeleteMapping("/users/{username}")
    public ApiResponse<UserDeleteResponseDto> deleteUser(
            @PathVariable String username) {

        log.info("DELETE /users/{} - 유저 삭제 요청 - 요청자: {}", username, JwtUserContext.getUsernameFromHeader());

        userServiceV1.deleteUser(username, JwtUserContext.getUsernameFromHeader());

        log.info("DELETE /users/{} - 유저 삭제 완료 (Soft Delete)", username);
        return ApiResponse.noContent();
    }

}
