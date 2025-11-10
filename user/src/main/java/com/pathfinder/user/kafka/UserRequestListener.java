package com.pathfinder.user.kafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.user.application.UserServiceV1;
import com.pathfinder.user.application.dto.request.UserInfoRequestDto;
import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.presentation.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRequestListener {
    private static final String REQUEST_TOPIC = "user-info-request-topic";

    private final ObjectMapper objectMapper;
    private final UserServiceV1 userService; // 캐싱 로직이 포함된 서비스 주입

    /**
     * 배송 담당자 서비스의 요청을 수신하고 응답을 돌려줍니다.
     */
    @KafkaListener(topics = REQUEST_TOPIC, groupId = "user-info-processor-group")
    @SendTo("user-info-reply-topic")
    public String listenForUserInfoRequest(String requestJson) {

        String username = null;
        try {
            UserInfoRequestDto requestDto = objectMapper.readValue(requestJson, UserInfoRequestDto.class);
            username = requestDto.getUsername();

            log.info("[User Service] User Info 요청 수신 (Cache 적용) - Username: {}", username);

            // 2. UserServiceV1 호출: @Cacheable(value = "users", key = "#username")이 적용된 메서드
            UserEntity userEntity = userService.findUser(username);

            // 3. 응답 DTO로 변환
            UserResponseDto userInfo = UserResponseDto.of(userEntity);

            // 4. 응답 객체를 JSON으로 변환하여 반환 (@SendTo)
            return objectMapper.writeValueAsString(userInfo);

        } catch (Exception e) {
            log.error("[User Service] 사용자 조회 및 응답 처리 중 오류 발생 - Username: {}", username, e);
            // 오류 발생 시 에러 메시지 응답
            return "{\"error\": \"User Lookup Failed\", \"username\": \"" + username + "\"}";
        }
    }
}