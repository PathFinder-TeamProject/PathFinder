package com.pathfinder.message.presentation.controller;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.message.application.MessageService;
import com.pathfinder.message.presentation.dto.request.MessageCreateDto;
import com.pathfinder.message.presentation.dto.request.MessageUpdateDto;
import com.pathfinder.message.presentation.dto.response.MessageResponseDto;
import com.pathfinder.message.presentation.enums.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/slack-messages")
@RequiredArgsConstructor
public class MessageControllerV1 {

    private final MessageService messageService;

    /** 메시지 생성 */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponseDto>> createMessage(@RequestBody MessageCreateDto dto) {
        return ResponseEntity
                .status(ApiStatus.CREATED.getCode())
                .body(ApiResponse.success(messageService.createMessage(dto)));
    }

    /** 메시지 단건 조회 */
    @GetMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponseDto>> getMessage(@PathVariable UUID messageId) {
        return ResponseEntity
                .ok(ApiResponse.success(messageService.getMessage(messageId)));
    }

    /** 메시지 전체 조회 (페이징 지원) */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageResponseDto>>> getAllMessages(Pageable pageable) {
        return ResponseEntity
                .ok(ApiResponse.success(messageService.getAllMessages(pageable)));
    }

    /** 메시지 수정 */
    @PutMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponseDto>> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateDto dto) {

        return ResponseEntity
                .ok(ApiResponse.success(messageService.updateMessage(messageId, dto)));
    }

    /** 메시지 삭제 */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity
                .ok(ApiResponse.success(null));
    }
}
