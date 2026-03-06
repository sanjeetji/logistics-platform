package com.logistics.compliance.controller;

import com.logistics.compliance.dto.CreatePODRequest;
import com.logistics.compliance.model.ProofOfDelivery;
import com.logistics.compliance.service.ProofOfDeliveryService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pod")
@RequiredArgsConstructor
public class ProofOfDeliveryController {

    private final ProofOfDeliveryService podService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProofOfDelivery>> createPOD(@Valid @RequestBody CreatePODRequest request) {
        ProofOfDelivery pod = podService.createPOD(
                request.getOrderId(),
                request.getRecipientName(),
                request.getSignature(),
                request.getLatitude(),
                request.getLongitude(),
                request.getPhotoUrl(),
                request.getNotes());
        return ResponseEntity.ok(ApiResponse.success(pod, "POD created successfully"));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ProofOfDelivery>> getPODByOrderId(@PathVariable String orderId) {
        ProofOfDelivery pod = podService.getPODByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(pod));
    }

    @PutMapping("/{podId}/verify")
    public ResponseEntity<ApiResponse<ProofOfDelivery>> verifyPOD(
            @PathVariable String podId,
            @RequestParam String verifiedBy) {
        ProofOfDelivery pod = podService.verifyPOD(podId, verifiedBy);
        return ResponseEntity.ok(ApiResponse.success(pod, "POD verified"));
    }

    @GetMapping("/unverified")
    public ResponseEntity<ApiResponse<List<ProofOfDelivery>>> getUnverifiedPODs() {
        List<ProofOfDelivery> pods = podService.getUnverifiedPODs();
        return ResponseEntity.ok(ApiResponse.success(pods));
    }
}
