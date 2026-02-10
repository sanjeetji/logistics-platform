package com.logistics.driver.controller;

import com.logistics.driver.dto.LocationUpdate;
import com.logistics.driver.service.LocationStreamingService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driver/location")
@RequiredArgsConstructor
@Slf4j
public class LocationStreamingController {

    private final LocationStreamingService locationStreamingService;

    /**
     * REST endpoint for location updates (fallback for HTTP)
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateLocation(@RequestBody LocationUpdate locationUpdate) {
        locationStreamingService.processLocationUpdate(locationUpdate);
        return ResponseEntity.ok(ApiResponse.success("Location updated", "Location processed successfully"));
    }
}

/**
 * WebSocket controller for real-time location streaming
 */
@Controller
@RequiredArgsConstructor
@Slf4j
class LocationWebSocketController {

    private final LocationStreamingService locationStreamingService;

    /**
     * WebSocket message handler for location updates
     * Clients send to: /app/location/update
     */
    @MessageMapping("/location/update")
    public void handleLocationUpdate(@Payload LocationUpdate locationUpdate) {
        log.debug("Received location update via WebSocket from driver: {}", locationUpdate.getDriverId());
        locationStreamingService.processLocationUpdate(locationUpdate);
    }
}
