package com.logistics.sla.service;

import com.logistics.sla.model.SLABreach;
import com.logistics.sla.repository.SLABreachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAMonitoringService {

    private final SLABreachRepository breachRepository;

    /**
     * Scheduled job to monitor SLA breaches
     * Runs every 15 minutes
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void monitorSLABreaches() {
        log.info("Running SLA breach monitoring");
        
        // TODO: Fetch active orders and check SLA compliance
        // Check pickup time, delivery time, response time SLAs
        
        List<SLABreach> activeBreaches = breachRepository.findByResolvedFalse();
        
        for (SLABreach breach : activeBreaches) {
            escalateBreach(breach);
        }
    }

    private void escalateBreach(SLABreach breach) {
        LocalDateTime now = LocalDateTime.now();
        long minutesSinceBreach = java.time.Duration.between(breach.getBreachTime(), now).toMinutes();

        // Escalation hierarchy: 15min -> L1, 30min -> L2, 60min -> L3
        if (minutesSinceBreach >= 60 && breach.getEscalationLevel() < 3) {
            breach.setEscalationLevel(3);
            log.warn("SLA breach {} escalated to L3", breach.getId());
            // TODO: Send alert to senior management
        } else if (minutesSinceBreach >= 30 && breach.getEscalationLevel() < 2) {
            breach.setEscalationLevel(2);
            log.warn("SLA breach {} escalated to L2", breach.getId());
            // TODO: Send alert to team lead
        } else if (minutesSinceBreach >= 15 && breach.getEscalationLevel() < 1) {
            breach.setEscalationLevel(1);
            log.info("SLA breach {} escalated to L1", breach.getId());
            // TODO: Send alert to support team
        }

        breachRepository.save(breach);
    }

    @Transactional
    public void resolveBreach(Long breachId, String resolvedBy, String resolution) {
        SLABreach breach = breachRepository.findById(breachId)
                .orElseThrow(() -> new RuntimeException("Breach not found"));

        breach.setResolved(true);
        breach.setResolvedAt(LocalDateTime.now());
        breach.setResolvedBy(resolvedBy);
        breach.setResolution(resolution);

        breachRepository.save(breach);
        log.info("SLA breach {} resolved by {}", breachId, resolvedBy);
    }
}
