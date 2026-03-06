package com.logistics.payout.service;

import com.logistics.payment.model.CODSettlement;
import com.logistics.payment.repository.CODSettlementRepository;
import com.logistics.payout.client.OrderServiceClient;
import com.logistics.payout.client.PaymentServiceClient;
import com.logistics.payout.model.Payout;
import com.logistics.payout.repository.PayoutRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutGenerationService {

    private final PayoutRepository payoutRepository;
    private final CODSettlementRepository codSettlementRepository;
    private final OrderServiceClient orderClient;
    private final PaymentServiceClient paymentClient;

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.80"); // 80% to driver

    /**
     * Scheduled job to generate payouts for completed orders
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateDailyPayouts() {
        log.info("Starting daily payout generation job with advanced reconciliation");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = yesterday.withHour(23).withMinute(59).withSecond(59);

        try {
            ApiResponse<List<OrderServiceClient.OrderResponse>> response = orderClient.getCompletedOrders(startOfDay,
                    endOfDay);
            if (response.isSuccess() && response.getData() != null) {
                List<OrderServiceClient.OrderResponse> orders = response.getData();

                // 1. Process Individual Gig Drivers (Orders without partnerId)
                Map<String, List<OrderServiceClient.OrderResponse>> driverOrders = orders.stream()
                        .filter(o -> o.getDriverId() != null && o.getPartnerId() == null)
                        .collect(Collectors.groupingBy(OrderServiceClient.OrderResponse::getDriverId));

                driverOrders.forEach((driverId, oList) -> {
                    BigDecimal earnings = calculateEarnings(oList);
                    BigDecimal deductions = calculateCodDeductions(driverId);
                    BigDecimal finalAmount = earnings.subtract(deductions).max(BigDecimal.ZERO);

                    List<String> orderIds = oList.stream()
                            .map(OrderServiceClient.OrderResponse::getOrderId)
                            .collect(Collectors.toList());

                    createPayout(Long.parseLong(driverId), null, orderIds, finalAmount);
                });

                // 2. Process Carrier Partners (Orders with partnerId)
                Map<String, List<OrderServiceClient.OrderResponse>> partnerOrders = orders.stream()
                        .filter(o -> o.getPartnerId() != null)
                        .collect(Collectors.groupingBy(OrderServiceClient.OrderResponse::getPartnerId));

                partnerOrders.forEach((partnerId, oList) -> {
                    BigDecimal totalAmount = calculateEarnings(oList);
                    List<String> orderIds = oList.stream()
                            .map(OrderServiceClient.OrderResponse::getOrderId)
                            .collect(Collectors.toList());

                    createPayout(null, partnerId, orderIds, totalAmount);
                });
            }
        } catch (Exception e) {
            log.error("Error during advanced payout generation: {}", e.getMessage(), e);
        }
    }

    private BigDecimal calculateEarnings(List<OrderServiceClient.OrderResponse> orders) {
        BigDecimal totalGross = orders.stream()
                .map(o -> o.getPrice() != null ? o.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalGross.multiply(DEFAULT_COMMISSION_RATE);
    }

    private BigDecimal calculateCodDeductions(String driverId) {
        // Find all collected cash that hasn't been reconciled yet
        List<CODSettlement> collectedCash = codSettlementRepository.findByDriverIdAndStatus(
                driverId, CODSettlement.SettlementStatus.COLLECTED);

        return collectedCash.stream()
                .map(CODSettlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Payout createPayout(Long driverId, String partnerId, List<String> orderIds, BigDecimal amount) {
        Payout payout = Payout.builder()
                .driverId(driverId)
                .partnerId(partnerId)
                .amount(amount)
                .status(Payout.PayoutStatus.PENDING)
                .generatedAt(LocalDateTime.now())
                .tenantId("DEFAULT") // Should be pulled from context
                .build();

        log.info("Created Payout for {} {} with {} orders, amount {}",
                driverId != null ? "driver" : "partner",
                driverId != null ? driverId : partnerId,
                orderIds.size(), amount);

        return payoutRepository.save(payout);
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

        if (payout.getStatus() != Payout.PayoutStatus.APPROVED) {
            throw new RuntimeException("Payout must be approved before processing");
        }

        log.info("Processing payout for driver: {}, amount: {}", payout.getDriverId(), payout.getAmount());

        try {
            PaymentDtos.PayoutRequest request = PaymentDtos.PayoutRequest.builder()
                    .accountId(String.valueOf(payout.getDriverId()))
                    .amount(payout.getAmount())
                    .currency("USD")
                    .gatewayType(PaymentDtos.GatewayType.STRIPE)
                    .build();

            ApiResponse<Boolean> response = paymentClient.processPayout(request);

            if (response.isSuccess() && Boolean.TRUE.equals(response.getData())) {
                payout.setStatus(Payout.PayoutStatus.PAID);
                payout.setPaidAt(LocalDateTime.now());
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
