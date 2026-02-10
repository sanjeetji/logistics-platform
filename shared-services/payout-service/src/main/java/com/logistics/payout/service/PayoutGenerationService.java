package com.logistics.payout.service;

import com.logistics.payout.model.Payout;
import com.logistics.payout.repository.PayoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutGenerationService {

    private final PayoutRepository payoutRepository;

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

        return payoutRepository.save(payout);
    }

    @Transactional
    public void approvePayout(Long payoutId, String approvedBy) {
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
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));

        if (payout.getStatus() != Payout.PayoutStatus.APPROVED) {
            throw new RuntimeException("Payout must be approved before processing");
        }

        // TODO: Integrate with payment gateway for actual transfer

        payout.setStatus(Payout.PayoutStatus.PAID);
        payout.setPaidAt(LocalDateTime.now());

        payoutRepository.save(payout);
        log.info("Payout {} processed successfully", payoutId);
    }
}
