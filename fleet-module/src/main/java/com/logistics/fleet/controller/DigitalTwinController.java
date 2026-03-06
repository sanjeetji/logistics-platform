package com.logistics.fleet.controller;

import com.logistics.fleet.service.DigitalTwinService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fleet/digital-twin")
@RequiredArgsConstructor
public class DigitalTwinController {

    private final DigitalTwinService digitalTwinService;

    /**
     * Emits the real-time exact 3D coordinates for Digital Twin consumers (WebGL,
     * Unreal Engine).
     */
    @GetMapping("/snapshot")
    public ResponseEntity<ApiResponse<DigitalTwinService.DigitalTwinSnapshot>> getSnapshot() {
        DigitalTwinService.DigitalTwinSnapshot snapshot = digitalTwinService.getNetworkSnapshot();
        return ResponseEntity.ok(ApiResponse.success(snapshot));
    }
}
