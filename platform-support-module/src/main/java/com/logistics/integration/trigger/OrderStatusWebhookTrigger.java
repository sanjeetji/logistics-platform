package com.logistics.integration.trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.integration.model.WebhookConfig;
import com.logistics.integration.model.WebhookEvent;
import com.logistics.integration.repository.WebhookConfigRepository;
import com.logistics.integration.repository.WebhookEventRepository;
import com.logistics.integration.service.WebhookDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusWebhookTrigger {

    private final WebhookConfigRepository configRepository;
    private final WebhookEventRepository eventRepository;
    private final WebhookDispatcherService dispatcherService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-status-events", groupId = "webhook-trigger-group")
    @Transactional
    public void onOrderStatusChanged(String message) {
        log.info("Received order status event for webhook evaluation: {}", message);
        try {
            Map<String, Object> eventData = objectMapper.readValue(message, Map.class);
            String orderId = (String) eventData.get("orderId");
            String newStatus = (String) eventData.get("newStatus");
            String tenantId = (String) eventData.get("tenantId");

            if (tenantId == null) {
                log.debug("No tenantId present in order status event {}; skipping webhooks", orderId);
                return;
            }

            // Fetch all active webhooks for this tenant
            List<WebhookConfig> activeConfigs = configRepository.findByTenantId(tenantId)
                    .stream()
                    .filter(WebhookConfig::isActive)
                    .toList();

            for (WebhookConfig config : activeConfigs) {
                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .tenantId(tenantId)
                        .platform(config.getPlatform())
                        .eventType("ORDER_STATUS_CHANGED")
                        .payload(message) // Forward the raw JSON or transform it
                        .status(WebhookEvent.WebhookStatus.PENDING)
                        .retryCount(0)
                        .createdAt(LocalDateTime.now())
                        // Note: targetUrl requires a mapping of platform -> endpoint URL or needs to be
                        // stored in WebhookConfig
                        // For demonstration, we assume WebhookConfig holds an endpointUrl property or
                        // we derive it:
                        .targetUrl(deriveTargetUrl(config))
                        .build();

                eventRepository.save(webhookEvent);

                // Immediately attempt dispatch
                dispatcherService.dispatchWebhook(webhookEvent);
                log.info("Generated outbound webhook event {} for order {} to {}", webhookEvent.getEventId(), orderId,
                        config.getPlatform());
            }

        } catch (Exception e) {
            log.error("Failed to process order status event for webhooks: {}", e.getMessage(), e);
        }
    }

    private String deriveTargetUrl(WebhookConfig config) {
        // In a real system, the webhook endpoint URL should be part of the
        // WebhookConfig entity.
        // Assuming a fallback mock URL for local processing based on API keys.
        return "https://api." + config.getPlatform().name().toLowerCase() + ".com/webhooks/logistics";
    }
}
