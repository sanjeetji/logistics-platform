package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.dto.b2c.PublicTrackingResponse;
import com.logistics.bff.unified.service.b2c.PublicTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Public tracking controller for B2B customers
 */
@RestController
@RequestMapping("/api/v1/bff/public")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Tracking", description = "Public tracking endpoints for B2B customers")
public class PublicTrackingController {

    private final PublicTrackingService publicTrackingService;

    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Get public tracking information")
    public ResponseEntity<PublicTrackingResponse> getPublicTracking(
            @Parameter(description = "Tracking number") @PathVariable String trackingNumber,
            @Parameter(description = "Brand ID for white-labeling") @RequestParam(required = false) String brandId) {

        PublicTrackingResponse response = publicTrackingService.getPublicTracking(trackingNumber, brandId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
                .body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Public Tracking Service is UP");
    }
}
