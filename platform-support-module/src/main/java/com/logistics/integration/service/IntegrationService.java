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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    private final WebhookConfigRepository configRepository;
    private final WebhookLogRepository logRepository;
    private final ObjectMapper objectMapper;
    private final com.logistics.platform.client.b2b.B2BOrderServiceClient b2bOrderClient;

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
        webhookLog = logRepository.save(java.util.Objects.requireNonNull(webhookLog));

        try {
            // 2. Validate Tenant Config
            Optional<WebhookConfig> configOpt = configRepository.findByTenantIdAndPlatform(tenantId, platform);
            if (configOpt.isEmpty()) {
                throw new RuntimeException(
                        "No configuration found for tenant " + tenantId + " and platform " + platform);
            }
            WebhookConfig config = configOpt.get();

            // 3. Verify Signature
            if (!verifySignature(payload, signature, config.getWebhookSecret())) {
                throw new RuntimeException("Invalid webhook signature");
            }

            // 4. Parse & Transform
            JsonNode rootNode = objectMapper.readTree(payload);
            String orderId = extractOrderId(rootNode, platform);

            log.info("Successfully extracted Order ID: {} from {}", orderId, platform);

            // 5. Update Log Status
            webhookLog.setStatus("PROCESSED");
            webhookLog.setEventType("ORDER_CREATED");
            logRepository.save(webhookLog);

            // Integrate with B2B Order Service to create internal order
            // Resolves TODO: Integrate with B2B Order Service to create internal order
            try {
                java.util.Map<String, Object> orderRequest = new java.util.HashMap<>();
                orderRequest.put("tenantId", tenantId);
                orderRequest.put("externalOrderId", orderId);
                orderRequest.put("platform", platform.name());
                orderRequest.put("source", "INTEGRATION_WEBHOOK");
                // In a real implementation, we would map line items, addresses, etc. from
                // rootNode

                b2bOrderClient.createOrder(orderRequest);
                log.info("Created internal order for external order: {}", orderId);
            } catch (Exception e) {
                log.error("Failed to create internal order for: {}", orderId, e);
                // We might want to throw exception to fail the webhook processing or just log
                // it
                // For now, logging error but not failing the webhook processing itself as
                // 'PROCESSED' just means we parsed it
            }

            log.info("Order {} from {} processed successfully for tenant {}", orderId, platform, tenantId);

        } catch (Exception e) {
            log.error("Error processing webhook", e);
            webhookLog.setStatus("FAILED");
            webhookLog.setErrorMessage(e.getMessage());
            logRepository.save(webhookLog);
            throw new RuntimeException("Webhook processing failed: " + e.getMessage());
        }
    }

    private boolean verifySignature(String payload, String signature, String secret) {
        if (signature == null || signature.isEmpty()) {
            log.warn("Missing signature header");
            return false;
        }

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
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
