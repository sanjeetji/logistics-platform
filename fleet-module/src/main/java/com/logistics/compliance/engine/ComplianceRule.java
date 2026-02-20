package com.logistics.compliance.engine;

import com.logistics.compliance.model.ComplianceStatus;

public interface ComplianceRule {
    boolean supports(String entityType);

    RuleResult evaluate(String entityId, Object context);

    record RuleResult(boolean passed, String message, ComplianceStatus suggestedStatus) {
    }
}
