package com.logistics.returns.controller;

import com.logistics.returns.dto.ReturnDTOs;
import com.logistics.returns.model.ReturnRequest;
import com.logistics.returns.service.ReturnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
public class ReturnsController {

    private final ReturnsService returnsService;

    @PostMapping
    public ResponseEntity<ReturnDTOs.ReturnResponseDTO> createReturn(@RequestBody ReturnDTOs.ReturnRequestDTO request) {
        ReturnRequest returnRequest = returnsService.requestReturn(request);
        return ResponseEntity.ok(mapToResponse(returnRequest));
    }

    @GetMapping("/{returnId}")
    public ResponseEntity<ReturnDTOs.ReturnResponseDTO> getReturn(@PathVariable String returnId) {
        ReturnRequest returnRequest = returnsService.getReturnById(returnId);
        return ResponseEntity.ok(mapToResponse(returnRequest));
    }

    @PatchMapping("/{returnId}/status")
    public ResponseEntity<ReturnDTOs.ReturnResponseDTO> updateStatus(
            @PathVariable String returnId,
            @RequestBody ReturnDTOs.UpdateStatusRequest request) {
        ReturnRequest returnRequest = returnsService.updateStatus(returnId, request.getStatus(),
                request.getAdminNotes());
        return ResponseEntity.ok(mapToResponse(returnRequest));
    }

    private ReturnDTOs.ReturnResponseDTO mapToResponse(ReturnRequest entity) {
        return ReturnDTOs.ReturnResponseDTO.builder()
                .returnId(entity.getReturnId())
                .orderId(entity.getOrderId())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .refundAmount(entity.getRefundAmount())
                .requestedAt(entity.getRequestedAt())
                .message("Current status: " + entity.getStatus())
                .build();
    }
}
