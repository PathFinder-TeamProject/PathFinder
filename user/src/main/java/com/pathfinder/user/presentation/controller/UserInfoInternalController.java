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

    /**
     * 내부 서비스용 사용자 정보 조회 API
     * DeliveryManager 서비스에서 FeignClient로 호출
     * @param username 조회할 사용자명
     * @return 사용자 정보 DTO
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String username) {
        log.info("[User Service] 사용자 정보 조회 요청 (Feign) - Username: {}", username);

        try {
            // @Cacheable이 적용된 메서드 호출 (캐싱 유지)
            UserEntity userEntity = userService.findUser(username);
            UserResponseDto userInfo = UserResponseDto.of(userEntity);

            log.info("[User Service] 사용자 정보 조회 완료 (Feign) - Username: {}", username);
            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            log.error("[User Service] 사용자 조회 중 오류 발생 - Username: {}", username, e);
            return ResponseEntity.notFound().build();
        }
    }
}