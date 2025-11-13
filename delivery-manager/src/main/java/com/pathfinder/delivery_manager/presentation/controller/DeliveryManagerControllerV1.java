package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerCreateRequestDto;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerUpdateRequestDto;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery-managers")
@Tag(name = "배송담당자 API", description = "허브 및 업체 소속 배송담당자의 등록, 수정, 삭제, 조회 기능을 제공합니다.")
public class DeliveryManagerControllerV1 {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    public DeliveryManagerControllerV1(DeliveryManagerServiceV1 deliveryManagerService) {
        this.deliveryManagerService = deliveryManagerService;
    }
    @Operation(
            summary = "배송담당자 등록",
            description = "허브 또는 업체 소속 배송담당자를 등록합니다. <br>**권한:** MASTER, HUB_MANAGER",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryManagerResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @PostMapping
    public ApiResponse<DeliveryManagerResponseDto> createManager(
            @Valid @RequestBody DeliveryManagerCreateRequestDto requestDto) {
        return ApiResponse.success(deliveryManagerService.createDeliveryManager(requestDto));
    }

    @Operation(
            summary = "배송담당자 단건 조회 (ID)",
            description = "UUID 기반으로 배송담당자 정보를 조회합니다. <br>**권한:** MASTER, HUB_MANAGER, 본인",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryManagerResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
            }
    )
    @GetMapping("/id/{id}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable UUID deliveryManagerId) {
        return ApiResponse.success(deliveryManagerService.getManagerById(deliveryManagerId));
    }

    @Operation(
            summary = "배송담당자 단건 조회 (username)",
            description = "username 기반으로 배송담당자 정보를 조회합니다. <br>**권한:** MASTER, HUB_MANAGER, 본인",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryManagerResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
            }
    )
    @GetMapping("/username/{username}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable String username) {
        return ApiResponse.success(deliveryManagerService.getManagerByUsername(username));
    }

    @Operation(
            summary = "배송담당자 전체 조회 및 검색",
            description = "허브 ID별, 상태, 정렬 기준으로 배송담당자 목록을 조회합니다. <br>**권한:** MASTER, HUB_MANAGER",
            parameters = {
                    @Parameter(name = "hubId", description = "허브 UUID (선택)"),
                    @Parameter(name = "page", description = "페이지 번호 (기본값: 1)"),
                    @Parameter(name = "size", description = "페이지 크기 (기본값: 10)"),
                    @Parameter(name = "sortBy", description = "정렬 기준 (기본값: createdAt)"),
                    @Parameter(name = "status", description = "배송담당자 상태 (기본값: approve)"),
                    @Parameter(name = "isAsc", description = "정렬 방향 (기본값: false)")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = Page.class)))
            }
    )
    // 전체 목록 조회 + 검색
    @GetMapping
    public ApiResponse<Page<DeliveryManagerResponseDto>> getAllManagers(
            @RequestParam(required = false) UUID hubId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "status", defaultValue = "approve") String status,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc

    ) {
        return ApiResponse.success(deliveryManagerService.getAllManagers(hubId, page, size, sortBy, isAsc));
    }

    @Operation(
            summary = "배송담당자 정보 수정",
            description = "배송담당자 정보를 수정합니다. <br>**권한:** MASTER, HUB_MANAGER(본인 허브 한정)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryManagerResponseDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
            }
    )
    @PutMapping("/{deliveryManagerId}")
    public ApiResponse<DeliveryManagerResponseDto> updateManager(
            @PathVariable UUID deliveryManagerId,
            @Valid @RequestBody DeliveryManagerUpdateRequestDto requestDto) {
        System.out.println("requestDto : " + requestDto);
        return ApiResponse.success(deliveryManagerService.updateManager(deliveryManagerId, requestDto));
    }
    @Operation(
            summary = "배송담당자 삭제 (논리적 삭제)",
            description = "배송담당자를 논리적으로 삭제합니다. <br>**권한:** MASTER, HUB_MANAGER(본인 허브 한정)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "대상 없음")
            }
    )
    @DeleteMapping("/{deliveryManagerId}")
    public ApiResponse<Void> deleteManager(@PathVariable UUID deliveryManagerId) {
        deliveryManagerService.deleteManager(deliveryManagerId);
        return ApiResponse.noContent();
    }

}