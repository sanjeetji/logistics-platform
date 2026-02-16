package com.logistics.webhook.consumer;

import com.logistics.webhook.service.WebhookDeliveryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookEventConsumer {

    private final WebhookDeliveryService deliveryService;

    @KafkaListener(topics = "webhook.delivery.request", groupId = "webhook-worker-group")
    public void handleWebhookRequest(WebhookRequest request) {
        log.info("Received webhook delivery request for topic: {}", request.getEventType());
        deliveryService.deliverWebhook(request.getTargetUrl(), request.getEventType(), request.getPayload());
    }

    @Data
    public static class WebhookRequest {
        private String targetUrl;
        private String eventType;
        private String payload;
    }
}
