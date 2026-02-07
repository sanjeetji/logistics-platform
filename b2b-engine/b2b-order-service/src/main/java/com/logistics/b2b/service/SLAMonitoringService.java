package com.logistics.b2b.service;

import com.logistics.b2b.model.B2BOrder;
import com.logistics.b2b.model.SLAStatus;
import com.logistics.b2b.repository.B2BOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for SLA monitoring and alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SLAMonitoringService {

    private final B2BOrderRepository orderRepository;
    
    private static final long AT_RISK_HOURS = 2; // 2 hours before deadline

    /**
     * Update SLA status for an order
     */
    @Transactional
    public void updateSLAStatus(B2BOrder order) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = order.getSlaDeadline();

        if (now.isAfter(deadline)) {
            order.setSlaStatus(SLAStatus.BREACHED);
            log.warn("SLA BREACHED for order: {}", order.getOrderId());
        } else {
            long hoursUntilDeadline = ChronoUnit.HOURS.between(now, deadline);
            if (hoursUntilDeadline <= AT_RISK_HOURS) {
                order.setSlaStatus(SLAStatus.AT_RISK);
                log.info("SLA AT RISK for order: {} ({} hours remaining)", order.getOrderId(), hoursUntilDeadline);
            } else {
                order.setSlaStatus(SLAStatus.ON_TIME);
            }
        }
    }

    /**
     * Scheduled task to monitor all active orders
     * Runs every 15 minutes
     */
    @Scheduled(fixedRate = 900000) // 15 minutes
    @Transactional
    public void monitorAllOrders() {
        log.info("Running SLA monitoring check...");

        List<B2BOrder> activeOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus().name().equals("SCHEDULED") || 
                                order.getStatus().name().equals("IN_PROGRESS"))
                .toList();

        int breached = 0;
        int atRisk = 0;

        for (B2BOrder order : activeOrders) {
            SLAStatus oldStatus = order.getSlaStatus();
            updateSLAStatus(order);
            
            if (order.getSlaStatus() != oldStatus) {
                orderRepository.save(order);
                
                if (order.getSlaStatus() == SLAStatus.BREACHED) {
                    breached++;
                    // TODO: Send alert notification
                } else if (order.getSlaStatus() == SLAStatus.AT_RISK) {
                    atRisk++;
                    // TODO: Send warning notification
                }
            }
        }

        log.info("SLA monitoring complete. Breached: {}, At Risk: {}", breached, atRisk);
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
