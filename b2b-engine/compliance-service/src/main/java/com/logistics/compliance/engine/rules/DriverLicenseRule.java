package com.logistics.compliance.engine.rules;

import com.logistics.compliance.engine.ComplianceRule;
import com.logistics.compliance.model.ComplianceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DriverLicenseRule implements ComplianceRule {

    @Override
    public boolean supports(String entityType) {
        return "DRIVER".equalsIgnoreCase(entityType);
    }

    @Override
    public RuleResult evaluate(String entityId, Object context) {
        log.info("Evaluating Driver License Rule for: {}", entityId);

        // Mock logic: In reality, check DB or external API
        if (entityId.contains("INVALID")) {
            return new RuleResult(false, "Driver license is invalid or expired", ComplianceStatus.NON_COMPLIANT);
        }

        return new RuleResult(true, "Driver license valid", ComplianceStatus.COMPLIANT);
    }
}
