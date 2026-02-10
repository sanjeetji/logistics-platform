package com.logistics.integration.service;

import com.logistics.integration.model.WebhookEvent;
import com.logistics.integration.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDispatcherService {

    private final WebhookEventRepository webhookEventRepository;
    private final RestTemplate restTemplate;

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final int[] BACKOFF_DELAYS = { 1, 2, 4, 8, 16 }; // seconds

    @Async
    public void dispatchWebhook(WebhookEvent event) {
        log.info("Dispatching webhook event {} to {}", event.getId(), event.getTargetUrl());

        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                boolean success = sendWebhook(event);

                if (success) {
                    event.setStatus(WebhookEvent.WebhookStatus.DELIVERED);
                    event.setDeliveredAt(LocalDateTime.now());
                    webhookEventRepository.save(event);
                    log.info("Webhook {} delivered successfully", event.getId());
                    return;
                }
            } catch (Exception e) {
                log.error("Webhook delivery attempt {} failed for event {}", attempt + 1, event.getId(), e);
                event.setRetryCount(attempt + 1);
                event.setLastError(e.getMessage());
                webhookEventRepository.save(event);
            }

            // Exponential backoff
            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                try {
                    Thread.sleep(BACKOFF_DELAYS[attempt] * 1000L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // All retries exhausted
        event.setStatus(WebhookEvent.WebhookStatus.FAILED);
        webhookEventRepository.save(event);
        log.error("Webhook {} failed after {} attempts", event.getId(), MAX_RETRY_ATTEMPTS);
    }

    private boolean sendWebhook(WebhookEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Add signature for security
        headers.set("X-Webhook-Signature", generateSignature(event));
        headers.set("X-Event-Type", event.getEventType());

        HttpEntity<String> request = new HttpEntity<>(event.getPayload(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    event.getTargetUrl(),
                    request,
                    String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Webhook delivery failed", e);
        }
    }

    private String generateSignature(WebhookEvent event) {
        // TODO: Implement HMAC signature for webhook security
        return "signature-placeholder";
    }
}
