package com.pathfinder.user.presentation.controller;

import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.presentation.dto.ApiResponse;
import com.pathfinder.user.presentation.dto.response.SignupResponseDto;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import com.pathfinder.user.presentation.dto.response.UserUpdateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.UUID;

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
        /*        return ResponseEntity.ok(Map.of(
                "userId", UUID.randomUUID().toString(),
                "email", request.getEmail(),
                "username", request.getUsername(),
                "name", request.getName(),
                "organization", request.getOrganization(),
                "role", request.getRole(),
                "status", "PENDING",
                "message", "회원가입 요청이 등록되었습니다. (더미 응답)"
        ));*/
    }

    // 사용자 정보 조회 (더미)
    @GetMapping("/{email}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String email) {
        if(email.contains("spartahub.com")){
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "name", "홍길동",
                    "organization", "스파르타허브",
                    "role", UserRoleEnum.HUB_MANAGER,
                    "status", "APPROVED"
            ));
        }else if(email.contains("spartaCompany.com")){
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "name", "홍길동",
                    "organization", "주)스파르타",
                    "role", UserRoleEnum.COMPANY_MANAGER,
                    "status", "APPROVED"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "name", "홍길동",
                    "organization", "스파르타물류",
                    "role", UserRoleEnum.DELIVERY_MANAGER,
                    "status", "APPROVED"
            ));
        }
    }

   /* @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ApiResponse.created(userServiceV1.signup(requestDto));
    }
    @GetMapping("/v1/users")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                                                          @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                          @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ApiResponse.ok(userServiceV1.getUserList(page - 1, size, sortBy, isAsc));
    }
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUser(@RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.ok(userServiceV1.updateUser(userUpdateRequestDto, userDetails.getUser()));
    }*/
}
