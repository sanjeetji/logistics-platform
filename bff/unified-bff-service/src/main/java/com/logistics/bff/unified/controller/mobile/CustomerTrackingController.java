package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.mobile.TrackingServiceClient;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile Customer Tracking Controller
 */
@RestController
@RequestMapping("/api/v1/mobile/customer/track")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Tracking", description = "Tracking for customer mobile app")
public class CustomerTrackingController {

    private final TrackingServiceClient trackingClient;

    @GetMapping("/{orderId}")
    @Operation(summary = "Track order")
    public ResponseEntity<TrackingInfoDTO> trackOrder(@PathVariable String orderId) {
        log.info("Mobile customer tracking request: {}", orderId);
        return ResponseEntity.ok(trackingClient.getTrackingByOrderId(orderId));
    }
}
