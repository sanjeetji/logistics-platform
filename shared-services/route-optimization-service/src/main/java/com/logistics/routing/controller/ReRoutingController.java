package com.logistics.routing.controller;

import com.logistics.platform.common.dto.ApiResponse;
import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.dto.ReRoutingResponse;
import com.logistics.routing.rerouting.DynamicReRoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Re-routing Controller
 */
@RestController
@RequestMapping("/api/v1/routes/reroute")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Re-routing", description = "Dynamic route re-optimization APIs")
public class ReRoutingController {

    private final DynamicReRoutingService reRoutingService;

    /**
     * Trigger manual re-routing
     */
    @PostMapping
    @Operation(summary = "Trigger re-routing", description = "Manually trigger route re-optimization")
    public ResponseEntity<ApiResponse<ReRoutingResponse>> triggerReRouting(
            @RequestBody ReRoutingRequest request) {
        
        log.info("Manual re-routing request: route={}, trigger={}", 
            request.getRouteId(), request.getTrigger());
        
        ReRoutingResponse response = reRoutingService.triggerReRouting(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get re-routing status
     */
    @GetMapping("/{reRoutingId}")
    @Operation(summary = "Get re-routing status", description = "Get status of a re-routing operation")
    public ResponseEntity<ApiResponse<ReRoutingResponse>> getReRoutingStatus(
            @PathVariable String reRoutingId) {
        
        // In real implementation, fetch from database
        log.debug("Fetching re-routing status: {}", reRoutingId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
