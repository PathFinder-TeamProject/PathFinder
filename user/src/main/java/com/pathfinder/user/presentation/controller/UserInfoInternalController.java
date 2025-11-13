package com.pathfinder.user.presentation.controller;
import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class UserInfoInternalController {

    private final UserServiceV1 userService;

    //내부 서비스용 사용자 정보 조회 API
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String username) {

        try {
            UserResponseDto userInfo = UserResponseDto.of(userService.findUser(username));
            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}