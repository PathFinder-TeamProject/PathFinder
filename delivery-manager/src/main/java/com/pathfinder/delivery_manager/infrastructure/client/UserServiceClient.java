package com.pathfinder.delivery_manager.infrastructure.client;
import com.pathfinder.delivery_manager.infrastructure.config.FeignClientConfig;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        name = "user-service",      // Eureka에 등록된 서비스 이름
        path = "/internal/users",    // 기본 경로: Controller의 @RequestMapping과 일치
        configuration = FeignClientConfig .class
)
public interface UserServiceClient {

    /**
     * 사용자 정보 조회
     * 실제 호출 URL: http://user-service/internal/users/{username}
     *
     * @param username 조회할 사용자명
     * @return 사용자 정보 DTO
     */
    @GetMapping("/{username}")
    UserInfoDto getUserInfo(@PathVariable("username") String username);
}