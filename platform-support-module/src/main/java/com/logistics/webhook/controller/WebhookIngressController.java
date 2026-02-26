package com.logistics.webhook.controller;

import com.logistics.webhook.service.IncomingWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks/ingress")
@RequiredArgsConstructor
@Slf4j
public class WebhookIngressController {

    private final IncomingWebhookService incomingWebhookService;
    private final com.logistics.webhook.service.WebhookSignatureService signatureService;

    /**
     * Standard ingress endpoint for third-party systems to push events.
     * Example: POST /api/v1/webhooks/ingress/shopify/order.created
     */
    @PostMapping("/{provider}/{eventType}")
    public ResponseEntity<String> receiveWebhook(
            @PathVariable String provider,
            @PathVariable String eventType,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String signature,
            @RequestBody String rawPayload) {

        log.debug("Received webhook - Provider: {}, Event: {}, Signature: {}", provider, eventType, signature);

        // Optional: verify the signature using an external tenant secret
        // For the monolith ingress, we generally skip deep validation until the tenant
        // is identified
        // but robust validation should ensure it's not spoofed.

        try {
            // Convert simple JSON or forward to service
            // Here we just pass it as generic payload stub
            incomingWebhookService.processIncomingEvent(provider, eventType, Map.of("raw", rawPayload));
            return ResponseEntity.accepted().body("Webhook received and queued for processing");
        } catch (Exception e) {
            log.error("Failed to process incoming webhook from {}", provider, e);
            return ResponseEntity.internalServerError().body("Webhook processing failed");
        }
    }
}
