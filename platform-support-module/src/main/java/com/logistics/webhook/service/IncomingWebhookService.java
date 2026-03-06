package com.logistics.webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomingWebhookService {

    /**
     * Processes an incoming event from an external system (e.g., Shopify, SAP).
     */
    public void processIncomingEvent(String provider, String eventType, Map<String, Object> payload) {
        log.info("Received incoming webhook from provider: [{}], Event Type: [{}]", provider, eventType);

        // In a real scenario, this would:
        // 1. Validate the structure
        // 2. Publish to a Kafka topic like 'incoming.webhooks' for asynchronous
        // processing
        // 3. Or convert the payload to a generic IntegrationEvent and pass it to an
        // Order integration flow

        log.debug("Payload details: {}", payload);

        // Example handling
        if ("order.created".equalsIgnoreCase(eventType)) {
            log.info("Initiating Create Order flow from {} incoming webhook", provider);
            // Trigger order creation...
        }
    }
}
