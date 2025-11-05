package com.pathfinder.user.presentation.controller;

import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.domain.entity.UserDetailsImpl;
import com.pathfinder.user.presentation.dto.response.SignupResponseDto;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import com.pathfinder.user.presentation.dto.response.UserUpdateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserControllerV1 {
    private final UserServiceV1 userServiceV1;
    @PostMapping("/auth/register")
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
    }
}
