package com.pathfinder.user.presentation.controller;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.excpetion.UserErrorCode;
import com.pathfinder.user.application.excpetion.ValidationException;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.presentation.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        if (requestDto.getRole() == UserRoleEnum.DELIVERY_MANAGER
                && requestDto.getDeliveryManagerType() == null) {
            throw new ValidationException(UserErrorCode.MISSING_REQUIRED_FIELD,UserErrorCode.MISSING_REQUIRED_FIELD.getFormattedMessage("배송 담당자 타입"));
        }

        return ApiResponse.success(userServiceV1.signup(requestDto));
    }
    @GetMapping("/users")
    public ApiResponse<Page<UserResponseDto>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                             @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                                             @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ApiResponse.success(userServiceV1.getUserList(page - 1, size, sortBy, isAsc));
    }
    @PatchMapping("/users/{username}/confirm-member")
    public ApiResponse<UserStatusUpdateResponseDto> updateUserConfirm(@PathVariable String username,
                                                                                      @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userServiceV1.checkActive(userDetails.getUsername());
        return ApiResponse.success(userServiceV1.updateUserStatus(username, userStatusUpdateRequestDto, userDetails.getUser()));
    }
    @PatchMapping("/users/{username}/role")
    public ApiResponse<UserRoleUpdateResponseDto> updateUserRole(@PathVariable String username,
                                                                                 @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateRequestDto,
                                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userServiceV1.checkActive(userDetails.getUsername());
        return ApiResponse.success(userServiceV1.userRoleUpdate(username, userRoleUpdateRequestDto, userDetails.getUser()));
    }

    @GetMapping("/users/myInfo")
    public ApiResponse<UserResponseDto> getMyUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(userServiceV1.getUser(userDetails.getUser().getUsername(), userDetails.getUser()) );
    }

    @GetMapping("/users/{username}")
    public ApiResponse<UserResponseDto> getUserInfo(@PathVariable String username,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(userServiceV1.getUser(username, userDetails.getUser()) );
    }
    @PutMapping("/users/{username}")
    public ApiResponse<UserUpdateResponseDto> updateUserInfo(@PathVariable String username,
                                                                        @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(userServiceV1.updateUser(username, userUpdateRequestDto, userDetails.getUser()) );
    }
}
