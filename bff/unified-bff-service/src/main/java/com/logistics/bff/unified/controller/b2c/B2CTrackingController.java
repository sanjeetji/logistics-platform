package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.b2c.TrackingServiceClient;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * B2C Tracking Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/track")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Tracking", description = "Tracking for B2C customers")
public class B2CTrackingController {

    private final TrackingServiceClient trackingClient;

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Track by number")
    public ResponseEntity<TrackingInfoDTO> trackByNumber(@PathVariable String trackingNumber) {
        log.info("B2C tracking request for: {}", trackingNumber);
        return ResponseEntity.ok(trackingClient.getTrackingInfo(trackingNumber));
    }
}
