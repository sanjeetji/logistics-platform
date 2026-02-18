package com.logistics.routing.controller;

import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.dto.ReRoutingResponse;
import com.logistics.routing.rerouting.DynamicReRoutingService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routing")
@RequiredArgsConstructor
public class ReRoutingController {

    private final DynamicReRoutingService reRoutingService;

    @PostMapping("/reroute")
    public ResponseEntity<ApiResponse<ReRoutingResponse>> triggerReRouting(
            @Valid @RequestBody ReRoutingRequest request) {
        ReRoutingResponse response = reRoutingService.triggerReRouting(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Re-routing process completed"));
    }
}
