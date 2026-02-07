package com.logistics.integration.controller;

import com.logistics.integration.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final IntegrationService integrationService;

    @PostMapping("/{platform}/{tenantId}")
    public ResponseEntity<String> receiveWebhook(
            @PathVariable String platform,
            @PathVariable String tenantId,
            @RequestBody String payload,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifySignature,
            @RequestHeader(value = "X-WC-Webhook-Signature", required = false) String wooSignature) {

        String signature = (shopifySignature != null) ? shopifySignature : wooSignature;
        
        integrationService.processWebhook(tenantId, platform, payload, signature);
        
        return ResponseEntity.ok("Webhook received successfully");
    }
}
