package com.hub_service.presentation.controller;


import com.hub_service.application.HubServiceV1;
import com.hub_service.presentation.dto.request.HubRequestDto;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> deleteHub(@PathVariable UUID hudId){
        hubServiceV1.deleteHub(hudId);
        return ResponseEntity.ok("허브가 삭제되었습니다.");
    }

}
