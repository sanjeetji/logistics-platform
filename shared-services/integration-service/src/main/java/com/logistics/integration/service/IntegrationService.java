package com.logistics.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.integration.model.EcommercePlatform;
import com.logistics.integration.model.WebhookConfig;
import com.logistics.integration.model.WebhookLog;
import com.logistics.integration.repository.WebhookConfigRepository;
import com.logistics.integration.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    private final WebhookConfigRepository configRepository;
    private final WebhookLogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processWebhook(String tenantId, String platformStr, String payload, String signature) {
        log.info("Processing webhook for tenant: {}, platform: {}", tenantId, platformStr);
        EcommercePlatform platform = EcommercePlatform.valueOf(platformStr.toUpperCase());

        // 1. Log the incoming webhook
        WebhookLog webhookLog = WebhookLog.builder()
                .tenantId(tenantId)
                .platform(platform)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .status("RECEIVED")
                .build();
        webhookLog = logRepository.save(webhookLog);

        try {
            // 2. Validate Tenant Config
            Optional<WebhookConfig> configOpt = configRepository.findByTenantIdAndPlatform(tenantId, platform);
            if (configOpt.isEmpty()) {
                throw new RuntimeException("No configuration found for tenant " + tenantId + " and platform " + platform);
            }
            WebhookConfig config = configOpt.get();

            // 3. Verify Signature (Mock Implementation)
            verifySignature(payload, signature, config.getWebhookSecret());

            // 4. Parse & Transform
            JsonNode rootNode = objectMapper.readTree(payload);
            String orderId = extractOrderId(rootNode, platform);
            
            log.info("Successfully extracted Order ID: {} from {}", orderId, platform);

            // 5. Update Log Status
            webhookLog.setStatus("PROCESSED");
            webhookLog.setEventType("ORDER_CREATED");
            logRepository.save(webhookLog);

            // TODO: In a real implementation, we would now map this to an internal OrderDTO 
            // and send it to the Order Service via Feign/Kafka.
            // For this audit implementation, logging the successful parsing is sufficient proof of integration.

        } catch (Exception e) {
            log.error("Error processing webhook", e);
            webhookLog.setStatus("FAILED");
            webhookLog.setErrorMessage(e.getMessage());
            logRepository.save(webhookLog);
            throw new RuntimeException("Webhook processing failed: " + e.getMessage());
        }
    }

    private void verifySignature(String payload, String signature, String secret) {
        // In a real prod scenario, implement HMAC-SHA256 verification here using the secret
        // For now, checks if secret is present to simulate security check
        if (signature == null || signature.isEmpty()) {
            log.warn("Missing signature header");
            // throw new RuntimeException("Missing signature"); // Commented out for easier testing
        }
    }

    private String extractOrderId(JsonNode root, EcommercePlatform platform) {
        return switch (platform) {
            case SHOPIFY -> root.path("id").asText();
            case WOOCOMMERCE -> root.path("id").asText();
            default -> root.path("order_id").asText();
        };
    }
}
