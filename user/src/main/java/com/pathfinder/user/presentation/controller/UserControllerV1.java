package com.pathfinder.user.presentation.controller;

import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserControllerV1 {
    private final UserServiceV1 userServiceV1;
    // 회원가입 (더미)
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequestDto request) {
        return ResponseEntity.ok(Map.of(
                "userId", UUID.randomUUID().toString(),
                "email", request.getEmail(),
                "username", request.getUsername(),
                "name", request.getName(),
                "organization", request.getOrganization(),
                "role", request.getRole(),
                "status", "PENDING",
                "message", "회원가입 요청이 등록되었습니다. (더미 응답)"
        ));
    }

    // 로그인 (더미)
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", "dummy@sparta.world");
        return ResponseEntity.ok(Map.of(
                "userId", UUID.randomUUID().toString(),
                "email", email,
                "name", "홍길동",
                "organization", "스파르타물류",
                "role", UserRoleEnum.MASTER,
                "token", "dummy-jwt-token-for-" + email,
                "message", "로그인 성공 (더미 응답)"
        ));
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

//    @PostMapping("/auth/register")
//    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
//        return ApiResponse.created(userServiceV1.signup(requestDto));
//    }
//    @GetMapping("/v1/users")
//    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUserList(@RequestParam(value = "page", defaultValue = "1") int page,
//                                                                          @RequestParam(value = "size", defaultValue = "10") int size,
//                                                                          @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
//                                                                          @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
//        return ApiResponse.ok(userServiceV1.getUserList(page - 1, size, sortBy, isAsc));
//    }
//    @PatchMapping("/users/{userId}/role")
//    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUser(@RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto,
//                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ApiResponse.ok(userServiceV1.updateUser(userUpdateRequestDto, userDetails.getUser()));
//    }
}
