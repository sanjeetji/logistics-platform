package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.TrackingAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Tracking Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/track")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Tracking", description = "Advanced tracking for B2B clients")
public class B2BTrackingController {

    private final TrackingAggregationService trackingService;

    @GetMapping("/{orderId}/live")
    @Operation(summary = "Get live tracking info")
    public ResponseEntity<Map<String, Object>> getLiveTracking(@PathVariable String orderId) {
        log.info("B2B live tracking request for order: {}", orderId);
        return ResponseEntity.ok(trackingService.getLiveLocation(orderId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get tracking summary")
    public ResponseEntity<Map<String, Object>> getTrackingSummary(@RequestParam String tenantId) {
        log.info("B2B tracking summary request for tenant: {}", tenantId);
        return ResponseEntity.ok(trackingService.getTrackingSummary(tenantId));
    }
}
