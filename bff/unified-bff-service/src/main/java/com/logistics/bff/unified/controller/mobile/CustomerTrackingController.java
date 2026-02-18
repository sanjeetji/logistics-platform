package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.TrackingServiceClient;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile Customer Tracking Controller
 * Handles tracking operations for customer mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/customer/track")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Tracking", description = "Tracking for customer mobile app")
public class CustomerTrackingController {

    private final TrackingServiceClient trackingClient;

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Track parcel", description = "Track parcel by tracking number from mobile app")
    public ResponseEntity<TrackingInfoDTO> trackParcel(@PathVariable String trackingNumber) {
        log.info("Mobile customer tracking parcel: {}", trackingNumber);
        return ResponseEntity.ok(trackingClient.getTrackingByNumber(trackingNumber));
    }
}
