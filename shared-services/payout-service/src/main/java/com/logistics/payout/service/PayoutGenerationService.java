package com.logistics.payout.service;

import com.logistics.payout.model.Payout;
import com.logistics.payout.repository.PayoutRepository;
import com.logistics.platform.api.payment.PaymentClient;
import com.logistics.platform.common.dto.payment.PaymentDtos;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutGenerationService {

    private final PayoutRepository payoutRepository;
    private final PaymentClient paymentClient;

    /**
     * Scheduled job to generate payouts for completed orders
     * Runs daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateDailyPayouts() {
        log.info("Starting daily payout generation job");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = yesterday.withHour(23).withMinute(59).withSecond(59);

        // TODO: Fetch completed orders from order-service
        // For now, this is a stub implementation

        log.info("Payout generation completed for period: {} to {}", startOfDay, endOfDay);
    }

    @Transactional
    public Payout createPayout(Long driverId, List<String> orderIds, BigDecimal amount) {
        Payout payout = Payout.builder()
                .driverId(driverId)
                .amount(amount)
                .status(Payout.PayoutStatus.PENDING)
                .generatedAt(LocalDateTime.now())
                .build();

        // TODO: Link orders to payout

        return payoutRepository.save(Objects.requireNonNull(payout, "Payout must not be null"));
    }

    @Transactional
    public void approvePayout(Long payoutId, String approvedBy) {
        if (payoutId == null) {
            throw new IllegalArgumentException("Payout ID must not be null");
        }
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approved by must not be null");
        }
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));

        payout.setStatus(Payout.PayoutStatus.APPROVED);
        payout.setApprovedAt(LocalDateTime.now());
        payout.setApprovedBy(approvedBy);

        payoutRepository.save(payout);
        log.info("Payout {} approved by {}", payoutId, approvedBy);
    }

    @Transactional
    public void processPayout(Long payoutId) {
        if (payoutId == null) {
            throw new IllegalArgumentException("Payout ID must not be null");
        }
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));

        if (payout.getStatus() != Payout.PayoutStatus.APPROVED) { // Changed from PENDING to APPROVED based on original
                                                                  // flow
            throw new RuntimeException("Payout must be approved before processing");
        }

        log.info("Processing payout for driver: {}, amount: {}", payout.getDriverId(), payout.getAmount());

        try {
            PaymentDtos.PayoutRequest request = PaymentDtos.PayoutRequest.builder()
                    .accountId(String.valueOf(payout.getDriverId())) // Assuming driverId is the accountId for now
                    .amount(payout.getAmount())
                    .currency("USD") // Default currency
                    .gatewayType(PaymentDtos.GatewayType.STRIPE) // Default gateway
                    .build();

            ApiResponse<Boolean> response = paymentClient.processPayout(request);

            if (response != null && Boolean.TRUE.equals(response.getData())) {
                payout.setStatus(Payout.PayoutStatus.PAID); // Changed from COMPLETED to PAID to match existing enum
                payout.setPaidAt(LocalDateTime.now()); // Changed from setProcessedAt to setPaidAt
                log.info("Payout successful for driver: {}", payout.getDriverId());
            } else {
                payout.setStatus(Payout.PayoutStatus.FAILED);
                log.error("Payout failed for driver: {}", payout.getDriverId());
            }
        } catch (Exception e) {
            payout.setStatus(Payout.PayoutStatus.FAILED);
            log.error("Error processing payout for driver: {}", payout.getDriverId(), e);
        }

        payoutRepository.save(payout);
    }
}
