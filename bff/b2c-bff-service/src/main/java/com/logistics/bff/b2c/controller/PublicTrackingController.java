package com.logistics.bff.b2c.controller;

import com.logistics.bff.b2c.dto.PublicTrackingResponse;
import com.logistics.bff.b2c.service.PublicTrackingService;
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
 * No authentication required - accessible via public tracking link
 */
@RestController
@RequestMapping("/api/v1/bff/public")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Tracking", description = "Public tracking endpoints for B2B customers")
public class PublicTrackingController {

    private final PublicTrackingService trackingService;

    /**
     * Get public tracking information by tracking number
     * Used by B2B customers to track their parcels via web link
     * 
     * Features:
     * - No authentication required
     * - White-label branding support
     * - Includes parcel/packaging details
     * - Real-time location tracking
     * - Driver information (privacy-masked)
     * 
     * @param trackingNumber Unique tracking number (e.g., TRK-ABC-123456)
     * @param brandId Optional brand ID for white-labeling
     * @return Public tracking page data with brand customization
     */
    @GetMapping("/track/{trackingNumber}")
    @Operation(
        summary = "Get public tracking information",
        description = "Retrieve tracking details for B2B customers via public link. " +
                     "Supports white-label branding and includes parcel details, timeline, and live location."
    )
    public ResponseEntity<PublicTrackingResponse> getPublicTracking(
            @Parameter(description = "Tracking number", example = "TRK-ABC-123456")
            @PathVariable String trackingNumber,
            
            @Parameter(description = "Brand ID for white-labeling", example = "brand-001")
            @RequestParam(required = false) String brandId) {
        
        log.info("Public tracking request for: {}, brandId: {}", trackingNumber, brandId);

        PublicTrackingResponse response = trackingService.getPublicTracking(trackingNumber, brandId);

        // Set cache headers (1 minute)
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
            .body(response);
    }

    /**
     * Health check endpoint for public tracking page
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if public tracking service is available")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Public tracking service is operational");
    }
}
