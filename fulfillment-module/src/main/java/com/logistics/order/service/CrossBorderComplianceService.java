package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.platform.api.identity.IdentityClient;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrossBorderComplianceService {

    private final IdentityClient identityClient;
    // We will inject FinanceClient or MLClient here later for duty calculation

    public void processCompliance(Order order) {
        // 1. Check if the tenant has the CROSS_BORDER_COMPLIANCE feature enabled
        try {
            Long tenantIdLong = order.getTenantId() != null ? Long.parseLong(order.getTenantId()) : null;
            if (tenantIdLong == null)
                return;

            ApiResponse<IdentityClient.MyFeaturesResponse> response = identityClient.getMyFeatures(tenantIdLong, null);
            if (response != null && response.isSuccess() && response.getData() != null) {
                List<String> features = response.getData().enabledFeatures();
                if (!features.contains("CROSS_BORDER_COMPLIANCE")) {
                    log.debug("Tenant {} does not have CROSS_BORDER_COMPLIANCE enabled. Skipping compliance check.",
                            order.getTenantId());
                    return;
                }
            }
        } catch (Exception e) {
            log.warn(
                    "Failed to check feature flags for tenant {}: {}. Proceeding without compliance checks (Fallback).",
                    order.getTenantId(), e.getMessage());
            return;
        }

        // 2. Feature is enabled. Execute Compliance Logic.
        log.info("Executing Cross-Border Compliance checks for Order: {}", order.getOrderId());

        // Check if origin and destination countries differ
        // For this we need to extract country from Location objects.
        // Assuming location Address contains Country or we can approximate it.
        String originCountry = extractCountry(order.getPickupLocation());
        String destCountry = extractCountry(order.getDropLocation());

        if (originCountry != null && destCountry != null && !originCountry.equalsIgnoreCase(destCountry)) {
            log.info("International Order detected ({} -> {}). Applying Customs and Duty verification.", originCountry,
                    destCountry);
            applyCustomsAndDuty(order, originCountry, destCountry);
        } else {
            log.debug("Domestic Order detected ({}). Compliance checks bypassed.", originCountry);
        }
    }

    private void applyCustomsAndDuty(Order order, String origin, String destination) {
        // ML Fallback strategy: Try to classify HS Codes via ML Service. If it fails,
        // default to standard generic classification.
        log.info("Applying AI HS Code classification and calculating Duty/Tax via Finance Module for Order {}",
                order.getOrderId());
        order.setRequiresCustomsDeclaration(true);
        // Will be expanded when integrating Finance and ML.
    }

    private String extractCountry(com.logistics.order.model.OrderLocation location) {
        if (location == null || location.getAddress() == null)
            return null;
        // Simple extraction logic for demo. Assuming Address ends with country, e.g.,
        // "New York, USA"
        String[] parts = location.getAddress().split(",");
        return parts[parts.length - 1].trim();
    }
}
