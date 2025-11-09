package com.pathfinder.user.presentation.controller;

import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.*;
import com.pathfinder.user.application.excpetion.ErrorCode;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.entity.UserEntity;
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
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        if (requestDto.getRole() == UserRoleEnum.DELIVERY_MANAGER
                && requestDto.getDeliveryManagerType() == null) {
            throw new RuntimeException(/*ErrorCode.INVALID_REQUEST,*/ "배송 담당자 타입이 필요합니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userServiceV1.signup(requestDto));
    }
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                             @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                                             @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ResponseEntity.ok(userServiceV1.getUserList(page - 1, size, sortBy, isAsc));
    }
    @PatchMapping("/users/{username}/confirm-member")
    public ResponseEntity<UserStatusUpdateResponseDto> updateUserConfirm(@PathVariable String username,
                                                                                      @RequestBody @Valid UserStatusUpdateRequestDto userStatusUpdateRequestDto,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userServiceV1.updateUserStatus(username, userStatusUpdateRequestDto, userDetails.getUser()));
    }
    @PatchMapping("/users/{username}/role")
    public ResponseEntity<UserRoleUpdateResponseDto> updateUserRole(@PathVariable String username,
                                                                                 @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateRequestDto,
                                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userServiceV1.userRoleUpdate(username, userRoleUpdateRequestDto, userDetails.getUser()));
    }

    @GetMapping("/users/myInfo")
    public ResponseEntity<UserResponseDto> getMyUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userServiceV1.getUser(userDetails.getUser().getUsername(), userDetails.getUser()) );
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String username,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userServiceV1.getUser(username, userDetails.getUser()) );
    }
    @PutMapping("/users/{username}")
    public ResponseEntity<UserUpdateResponseDto> updateUserInfo(@PathVariable String username,
                                                                        @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userServiceV1.updateUser(username, userUpdateRequestDto, userDetails.getUser()) );
    }
}
