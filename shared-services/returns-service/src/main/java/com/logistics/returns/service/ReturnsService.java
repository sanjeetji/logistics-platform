package com.logistics.returns.service;

import com.logistics.returns.dto.ReturnDTOs;
import com.logistics.returns.model.ReturnRequest;
import com.logistics.returns.model.ReturnStatus;
import com.logistics.returns.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnsService {

    private final ReturnRequestRepository repository;

    @Transactional
    public ReturnRequest requestReturn(ReturnDTOs.ReturnRequestDTO dto) {
        log.info("Processing return request for Order: {}", dto.getOrderId());

        // 1. In a real system, validate Order existence via Feign Client to
        // Order-Service
        // 2. Mock Refund Amount Calculation (e.g., fetch from original order price)
        BigDecimal estimatedRefund = new BigDecimal("100.00"); // Mock

        ReturnRequest request = ReturnRequest.builder()
                .returnId("RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .reason(dto.getReason())
                .description(dto.getDescription())
                .proofImages(dto.getProofImages())
                .pickupAddress(dto.getPickupAddress())
                .pickupLatitude(dto.getPickupLatitude())
                .pickupLongitude(dto.getPickupLongitude())
                .refundAmount(estimatedRefund)
                .status(ReturnStatus.REQUESTED)
                .build();

        // Auto-Approver Logic (Simple Rule: Broken items < $50 auto-approve)
        if (dto.getReason().name().contains("DAMAGED") && estimatedRefund.compareTo(new BigDecimal("50")) < 0) {
            request.setStatus(ReturnStatus.APPROVED);
            log.info("Auto-approved return: {}", request.getReturnId());
        }

        return repository.save(request);
    }

    public ReturnRequest getReturnById(String returnId) {
        return repository.findByReturnId(returnId)
                .orElseThrow(() -> new RuntimeException("Return request not found: " + returnId));
    }

    @Transactional
    public ReturnRequest updateStatus(String returnId, ReturnStatus newStatus, String notes) {
        ReturnRequest request = getReturnById(returnId);
        log.info("Updating return {} status from {} to {}", returnId, request.getStatus(), newStatus);

        request.setStatus(newStatus);
        if (notes != null) {
            request.setAdminNotes(notes);
        }

        if (newStatus == ReturnStatus.REFUNDED) {
            request.setProcessedAt(LocalDateTime.now());
            // Trigger Payment Service Refund here
            log.info("Triggering refund of ${} for Return {}", request.getRefundAmount(), returnId);
        }

        if (newStatus == ReturnStatus.APPROVED) {
            // Trigger Reverse Logistics Order creation here
            log.info("Initiating RTO (Reverse Logistics) for Return {}", returnId);
        }

        return repository.save(request);
    }
}
