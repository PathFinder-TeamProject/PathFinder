package com.hub_service.presentation.controller;


import com.hub_service.application.HubServiceV1;
import com.hub_service.presentation.dto.response.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HubController {

    private final HubServiceV1 hubServiceV1;

    @GetMapping("/hubs")
    public ResponseEntity<List<HubResponseDto>> getHubs(){
        List<HubResponseDto> hubs = hubServiceV1.getAllHubs();
        return ResponseEntity.ok(hubs);
    }


}
