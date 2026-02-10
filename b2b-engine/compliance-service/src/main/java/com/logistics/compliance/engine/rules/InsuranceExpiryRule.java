package com.logistics.compliance.engine.rules;

import com.logistics.compliance.engine.ComplianceRule;
import com.logistics.compliance.model.ComplianceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class InsuranceExpiryRule implements ComplianceRule {

    @Override
    public boolean supports(String entityType) {
        return "VEHICLE".equalsIgnoreCase(entityType) || "DRIVER".equalsIgnoreCase(entityType);
    }

    @Override
    public RuleResult evaluate(String entityId, Object context) {
        log.info("Evaluating Insurance Expiry Rule for: {}", entityId);

        // Mock logic: Fail if ID ends with 'EXP'
        if (entityId.endsWith("EXP")) {
            return new RuleResult(false, "Insurance policy has expired", ComplianceStatus.NON_COMPLIANT);
        }

        return new RuleResult(true, "Insurance policy active", ComplianceStatus.COMPLIANT);
    }
}
