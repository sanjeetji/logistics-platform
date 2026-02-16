package com.logistics.b2b.service;

import com.logistics.b2b.model.OrderType;
import com.logistics.b2b.model.Priority;
import com.logistics.b2b.model.SLAConfig;
import com.logistics.b2b.repository.SLAConfigRepository;
import com.logistics.platform.client.rules.RulesEngineClient;
import com.logistics.platform.common.dto.rules.RuleFacts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLARuleService {

    private final SLAConfigRepository configRepository;
    private final RulesEngineClient rulesEngineClient;

    private static final int DEFAULT_SLA_MINUTES = 480; // 8 hours default

    /**
     * Resolves the SLA deadline based on order parameters.
     * Hierarchical resolution: Client-specific > Global.
     */
    public LocalDateTime calculateDeadline(Long clientId, OrderType orderType, Priority priority) {
        log.info("Calculating SLA deadline for Client: {}, Type: {}, Priority: {}", clientId, orderType, priority);

        // 1. Get base config from DB
        SLAConfig config = configRepository.findByClientIdAndOrderTypeAndPriority(clientId, orderType, priority)
                .or(() -> configRepository.findByClientIdIsNullAndOrderTypeAndPriority(orderType, priority))
                .orElse(null);

        // 2. Call Rules Engine for dynamic adjustments
        RuleFacts.SLAFact fact = RuleFacts.SLAFact.builder()
                .clientTier(clientId != null ? "PREMIUM" : "STANDARD") // Simplified mapping
                .orderType(orderType != null ? orderType.name() : "B2B")
                .priority(priority != null ? priority.name() : "NORMAL")
                .build();

        try {
            fact = rulesEngineClient.evaluateSla(fact);
        } catch (Exception e) {
            log.error("Error evaluating SLA rules, falling back to database config", e);
        }

        int minutes = fact.getTargetDurationMinutes() > 0 ? fact.getTargetDurationMinutes()
                : (config != null) ? config.getTargetDurationMinutes() : DEFAULT_SLA_MINUTES;

        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * Fetches the 'At Risk' threshold for an order
     */
    public int getAtRiskThreshold(Long clientId, OrderType orderType, Priority priority) {
        SLAConfig config = configRepository.findByClientIdAndOrderTypeAndPriority(clientId, orderType, priority)
                .or(() -> configRepository.findByClientIdIsNullAndOrderTypeAndPriority(orderType, priority))
                .orElse(null);

        return (config != null) ? config.getAtRiskThresholdMinutes() : 60; // 1 hour default
    }
}
