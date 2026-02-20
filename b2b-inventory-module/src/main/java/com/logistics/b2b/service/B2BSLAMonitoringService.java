package com.logistics.b2b.service;

import java.util.Objects;
import com.logistics.b2b.client.NotificationServiceClient;
import com.logistics.b2b.model.*;
import com.logistics.b2b.repository.B2BOrderRepository;
import com.logistics.b2b.repository.SLAEscalationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for SLA monitoring and alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2BSLAMonitoringService {

    private final B2BOrderRepository orderRepository;
    private final SLARuleService slaRuleService;
    private final SLAEscalationRepository escalationRepository;
    private final NotificationServiceClient notificationClient;

    /**
     * Update SLA status for an order
     */
    @Autowired
    @Lazy
    private B2BSLAMonitoringService self;

    /**
     * Update SLA status for an order
     */
    @Transactional
    public void updateSLAStatus(B2BOrder order) {
        if (order.getStatus() == B2BOrderStatus.ON_HOLD) {
            log.info("SLA Paused for order: {}", order.getOrderId());
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = order.getSlaDeadline();

        if (now.isAfter(deadline)) {
            if (order.getSlaStatus() != SLAStatus.BREACHED) {
                order.setSlaStatus(SLAStatus.BREACHED);
                triggerEscalation(order, SLAEscalation.EscalationLevel.CRITICAL);
            }
        } else {
            int atRiskThreshold = slaRuleService.getAtRiskThreshold(order.getClientId(), order.getOrderType(),
                    order.getPriority());
            long minutesUntilDeadline = ChronoUnit.MINUTES.between(now, deadline);

            if (minutesUntilDeadline <= atRiskThreshold) {
                if (order.getSlaStatus() == SLAStatus.ON_TIME) {
                    order.setSlaStatus(SLAStatus.AT_RISK);
                    triggerEscalation(order, SLAEscalation.EscalationLevel.WARNING);
                }
            } else {
                order.setSlaStatus(SLAStatus.ON_TIME);
            }
        }
    }

    private void triggerEscalation(B2BOrder order, SLAEscalation.EscalationLevel level) {
        log.warn("SLA Escalation [{}] for order: {}", level, order.getOrderId());
        SLAEscalation escalation = SLAEscalation.builder()
                .orderId(order.getOrderId())
                .level(level)
                .escalatedAt(LocalDateTime.now())
                .build();
        escalationRepository.save(Objects.requireNonNull(escalation));

        // Trigger notification
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("orderId", order.getOrderId());
            metadata.put("clientId", order.getClientId());
            metadata.put("escalationLevel", level.toString());

            notificationClient.sendNotification(NotificationServiceClient.SendNotificationRequest.builder()
                    .recipientId(order.getClientId().toString())
                    .recipientType("CLIENT")
                    .channel("PUSH")
                    .subject("SLA Escalation Alert - " + order.getOrderId())
                    .body(String.format("Order %s has reached escalation level: %s", order.getOrderId(), level))
                    .metadata(metadata)
                    .build());
            log.info("SLA escalation notification sent for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send SLA escalation notification for order: {}", order.getOrderId(), e);
        }
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void processOrderSLA(B2BOrder order) {
        SLAStatus oldStatus = order.getSlaStatus();
        updateSLAStatus(order);

        if (order.getSlaStatus() != oldStatus) {
            orderRepository.save(order);
        }
    }

    /**
     * Scheduled task to monitor all active orders
     * Runs every 15 minutes
     */
    @Scheduled(fixedRate = 900000)
    public void monitorAllOrders() {
        log.info("Running advanced SLA monitoring check...");

        List<B2BOrder> activeOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == B2BOrderStatus.SCHEDULED ||
                        order.getStatus() == B2BOrderStatus.IN_PROGRESS ||
                        order.getStatus() == B2BOrderStatus.APPROVED)
                .toList();

        for (B2BOrder order : activeOrders) {
            try {
                self.processOrderSLA(order);
            } catch (Exception e) {
                log.error("Error processing SLA for order: {}", order.getOrderId(), e);
            }
        }
    }

    /**
     * Get SLA compliance report
     */
    public SLAReport getSLAReport(Long clientId, LocalDateTime startDate, LocalDateTime endDate) {
        List<B2BOrder> orders = orderRepository.findByClientId(clientId).stream()
                .filter(o -> o.getCreatedAt().isAfter(startDate) && o.getCreatedAt().isBefore(endDate))
                .toList();

        long total = orders.size();
        long onTime = orders.stream().filter(o -> o.getSlaStatus() == SLAStatus.ON_TIME).count();
        long atRisk = orders.stream().filter(o -> o.getSlaStatus() == SLAStatus.AT_RISK).count();
        long breached = orders.stream().filter(o -> o.getSlaStatus() == SLAStatus.BREACHED).count();

        double complianceRate = total > 0 ? ((double) onTime / total) * 100 : 0;

        return SLAReport.builder()
                .totalOrders(total)
                .onTimeOrders(onTime)
                .atRiskOrders(atRisk)
                .breachedOrders(breached)
                .complianceRate(complianceRate)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class SLAReport {
        private long totalOrders;
        private long onTimeOrders;
        private long atRiskOrders;
        private long breachedOrders;
        private double complianceRate;
    }
}
