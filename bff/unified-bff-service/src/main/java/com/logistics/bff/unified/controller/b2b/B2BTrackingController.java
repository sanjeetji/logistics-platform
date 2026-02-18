package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.client.TrackingServiceClient;
import com.logistics.bff.unified.service.TrackingAggregationService;
import com.logistics.platform.dto.tracking.TrackingEventDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B2B Tracking Controller
 * Handles tracking operations for B2B clients
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/tracking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Tracking", description = "Tracking management for B2B clients")
public class B2BTrackingController {

    private final TrackingServiceClient trackingClient;
    private final TrackingAggregationService trackingAggregationService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get tracking info", description = "Get tracking information for an order")
    public ResponseEntity<TrackingInfoDTO> getTrackingInfo(@PathVariable String orderId) {
        log.info("Fetching tracking info for order: {}", orderId);
        return ResponseEntity.ok(trackingClient.getTrackingInfo(orderId));
    }

    @GetMapping("/live/{orderId}")
    @Operation(summary = "Get live location", description = "Get real-time location tracking for an order")
    public ResponseEntity<Map<String, Object>> getLiveLocation(@PathVariable String orderId) {
        log.info("Fetching live location for order: {}", orderId);
        return ResponseEntity.ok(trackingAggregationService.getLiveLocation(orderId));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get tracking analytics", description = "Get tracking analytics and metrics")
    public ResponseEntity<Map<String, Object>> getTrackingAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching tracking analytics from {} to {}", startDate, endDate);
        return ResponseEntity.ok(trackingAggregationService.getTrackingAnalytics(startDate, endDate));
    }

    @GetMapping("/events/{orderId}")
    @Operation(summary = "Get tracking events", description = "Get all tracking events for an order")
    public ResponseEntity<List<TrackingEventDTO>> getTrackingEvents(@PathVariable String orderId) {
        log.info("Fetching tracking events for order: {}", orderId);
        return ResponseEntity.ok(trackingClient.getTrackingByOrder(Long.parseLong(orderId)));
    }
}
