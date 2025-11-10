package com.pathfinder.delivery_manager.infrastructure.kafka;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.delivery_manager.application.dto.request.UserRequestDto;
import com.pathfinder.delivery_manager.presentation.dto.response.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.requestreply.RequestReplyMessageFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRequestProducer {

    // 사용자 정보 요청 토픽
    private static final String REQUEST_TOPIC = "user-info-request-topic";
    // 응답을 받을 토픽 (User Service에서 사용할 토픽)
    // ReplyingKafkaTemplate 사용 시, 응답 토픽 설정은 KafkaConfig에서 처리됩니다.
    private static final String REPLY_TOPIC = "user-info-reply-topic";
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * username을 기준으로 User Service에 정보를 요청하고 응답을 받는 메서드 (동기적)
     * @param username 요청할 사용자 이름
     * @return UserInfoDto (사용자 서비스에서 응답할 DTO)
     */
    public UserInfoDto requestUserInfo(String username) {
        try {
            UserRequestDto requestDto = UserRequestDto.builder().username(username).build();
            String jsonRequest = objectMapper.writeValueAsString(requestDto);

            Message<String> message = MessageBuilder
                    .withPayload(jsonRequest)
                    .setHeader(KafkaHeaders.TOPIC, REQUEST_TOPIC)
                    .setHeader(KafkaHeaders.REPLY_TOPIC, REPLY_TOPIC)
                    .build();
            RequestReplyMessageFuture<String, String> replyFuture =
                    replyingKafkaTemplate.sendAndReceive(message);
            Message<?> replyMessage = replyFuture.get(5, TimeUnit.SECONDS);
            String jsonResponse = (String) replyMessage.getPayload();
            return objectMapper.readValue(jsonResponse, UserInfoDto.class);

        } catch (Exception e) {
            log.error("[DM Service] Kafka Request-Reply 오류 발생", e);
            throw new RuntimeException("사용자 서비스로부터 정보를 가져오는 중 오류가 발생했습니다.", e);
        }
    }
}
