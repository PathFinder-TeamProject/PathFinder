package com.hub_service.presentation.controller;


import com.hub_service.application.HubServiceV1;
import com.hub_service.presentation.dto.request.HubRequestDto;
import com.hub_service.presentation.dto.response.HubResponseDto;
import com.hub_service.presentation.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HubController {

    private final HubServiceV1 hubServiceV1;

    @GetMapping("/hubs")
    public ResponseEntity<List<HubResponseDto>> getHubs(){
        return ResponseEntity.ok(hubServiceV1.getAllHubs());
    }

    @GetMapping("/hubs/{hubId}")
    public ResponseEntity<HubResponseDto> getHub(@PathVariable UUID hubId){
        return ResponseEntity.ok(hubServiceV1.getHubById(hubId));
    }

    @PostMapping("/hubs")
    public ResponseEntity<HubResponseDto> createHub(@RequestBody HubRequestDto requestDto){
        return ResponseEntity.ok(hubServiceV1.createHub(requestDto));
    }

    @PatchMapping("/hubs/{hubId}")
    public ResponseEntity<HubResponseDto> updateHub(@PathVariable UUID hubId, @RequestBody HubRequestDto requestDto){
        return ResponseEntity.ok(hubServiceV1.updateHub(hubId, requestDto));
    }

    @DeleteMapping("/hubs/{hudId}")
    public ResponseEntity<ResponseDto<String>> deleteHub(
            @PathVariable UUID hudId,
            @RequestParam(defaultValue = "master") String username
    ){
        hubServiceV1.deleteHub(hudId, username);
        return ResponseEntity.ok(ResponseDto.success("허브 삭제 완료", "허브가 논리적으로 삭제되었습니다."));
    }

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
