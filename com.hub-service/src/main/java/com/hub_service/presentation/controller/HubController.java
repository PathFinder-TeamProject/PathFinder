package com.hub_service.presentation.controller;


import com.hub_service.application.HubServiceV1;
import com.hub_service.presentation.dto.request.HubRequestDto;
import com.hub_service.presentation.dto.response.HubResponseDto;
import com.hub_service.presentation.dto.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "허브 관리 API", description = "허브 등록, 조회, 수정, 삭제 및 검색 기능 제공")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HubController {

    private final HubServiceV1 hubServiceV1;

    @Operation(summary = "허브 전체 조회", description = "등록된 모든 허브 목록을 조회합니다.")
    @GetMapping("/hubs")
    public ResponseEntity<List<HubResponseDto>> getHubs(){
        return ResponseEntity.ok(hubServiceV1.getAllHubs());
    }

    @Operation(summary = "허브 단건 조회", description = "허브 ID를 기준으로 단일 허브 정보를 조회합니다.")
    @Parameter(name = "hubId", description = "조회할 허브의 UUID", example = "3dd50a9f-ea15-41d3-83eb-a55b7e282a83")
    @GetMapping("/hubs/{hubId}")
    public ResponseEntity<HubResponseDto> getHub(@PathVariable UUID hubId){
        return ResponseEntity.ok(hubServiceV1.getHubById(hubId));
    }

    @Operation(summary = "허브 생성", description = "새로운 허브 정보를 등록합니다.")
    @PostMapping("/hubs")
    public ResponseEntity<HubResponseDto> createHub(@RequestBody HubRequestDto requestDto){
        return ResponseEntity.ok(hubServiceV1.createHub(requestDto));
    }
    @Operation(summary = "허브 수정", description = "허브 ID를 기준으로 허브 정보를 수정합니다.")
    @PatchMapping("/hubs/{hubId}")
    public ResponseEntity<HubResponseDto> updateHub(@PathVariable UUID hubId, @RequestBody HubRequestDto requestDto){
        return ResponseEntity.ok(hubServiceV1.updateHub(hubId, requestDto));
    }

    @Operation(summary = "허브 삭제", description = "허브 ID를 기준으로 허브를 논리적으로 삭제합니다.")
    @Parameter(name = "hudId", description = "삭제할 허브의 UUID")
    @Parameter(name = "username", description = "삭제 수행자 (기본값 master)", example = "master")
    @DeleteMapping("/hubs/{hudId}")
    public ResponseEntity<ResponseDto<String>> deleteHub(
            @PathVariable UUID hudId,
            @RequestParam(defaultValue = "master") String username
    ){
        hubServiceV1.deleteHub(hudId, username);
        return ResponseEntity.ok(ResponseDto.success("허브 삭제 완료", "허브가 논리적으로 삭제되었습니다."));
    }

    @Operation(summary = "허브 검색", description = "키워드, 정렬, 페이지 정보를 기준으로 허브 목록을 검색합니다.")
    @GetMapping("/hubs/search")
    public ResponseEntity<ResponseDto<Page<HubResponseDto>>> searchHubs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable =  PageRequest.of(page, size);
        Page<HubResponseDto> result = hubServiceV1.searchHubs(keyword, sortBy, pageable);
        return ResponseEntity.ok(ResponseDto.success("허브 검색 성공", result));

    }

}
