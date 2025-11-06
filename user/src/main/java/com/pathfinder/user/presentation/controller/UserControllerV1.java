package com.pathfinder.user.presentation.controller;

import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserRoleUpdateRequestDto;
import com.pathfinder.user.application.dto.request.UserStatusUpdateRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.presentation.dto.response.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.pathfinder.user.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserControllerV1 {
    @Value("${server.port}") // 애플리케이션이 실행 중인 포트를 주입받습니다.
    private String serverPort;

    private final UserServiceV1 userServiceV1;
    // 회원가입 (더미)
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ApiResponse.created(userServiceV1.signup(requestDto));
    }
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                                                          @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                                                          @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ApiResponse.ok(userServiceV1.getUserList(page - 1, size, sortBy, isAsc));
    }
    @PatchMapping("/users/{username}/confirm-member")
    public ResponseEntity<ApiResponse<UserStatusUpdateResponseDto>> updateUserConfirm(@PathVariable String username,
                                                                                      @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.updateUserStatus(username, userStatusUpdateRequestDto, userDetails.getUser()));
    }
    @PatchMapping("/users/{username}/role")
    public ResponseEntity<ApiResponse<UserRoleUpdateResponseDto>> updateUserRole(@PathVariable String username,
                                                                                 @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateRequestDto,
                                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.userRoleUpdate(username, userRoleUpdateRequestDto, userDetails.getUser()));
    }

    @GetMapping("/users/myInfo")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.getUser(userDetails.getUser().getUsername(), userDetails.getUser()) );
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserInfo(@PathVariable String username,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.getUser(username, userDetails.getUser()) );
    }
    @PutMapping("/users/{username}")
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUserInfo(@PathVariable String username,
                                                                        @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.updateUser(username, userUpdateRequestDto, userDetails.getUser()) );
    }
}
