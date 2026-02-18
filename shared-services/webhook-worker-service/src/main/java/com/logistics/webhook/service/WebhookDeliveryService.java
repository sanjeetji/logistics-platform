package com.logistics.webhook.service;

import com.logistics.webhook.model.WebhookLog;
import com.logistics.webhook.repository.WebhookLogRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryService {

    private final WebClient.Builder webClientBuilder;
    private final WebhookSignatureService signatureService;
    private final WebhookLogRepository logRepository;

    @Retry(name = "webhookRetry", fallbackMethod = "handleDeliveryFailure")
    public void deliverWebhook(String url, String eventType, String payload) {
        String eventId = UUID.randomUUID().toString();
        log.info("Attempting to deliver webhook: {} to {}", eventType, url);

        WebhookLog webhookLog = logRepository.findByEventId(eventId)
                .orElse(WebhookLog.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .targetUrl(url)
                        .payload(payload)
                        .attempts(0)
                        .status("PENDING")
                        .build());

        webhookLog.setAttempts(webhookLog.getAttempts() + 1);
        String signature = signatureService.generateSignature(payload);

        try {
            webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header("X-Logistics-Signature", signature)
                    .header("X-Logistics-Event-Type", eventType)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            webhookLog.setStatus("SUCCESS");
            webhookLog.setResponseCode(200);
            logRepository.save(webhookLog);
            log.info("Webhook delivered successfully: {}", eventId);

        } catch (Exception e) {
            webhookLog.setStatus("FAILED");
            webhookLog.setResponseBody(e.getMessage());
            logRepository.save(webhookLog);
            log.warn("Webhook delivery failed for {}: {}", eventId, e.getMessage());
            throw e; // Rethrow for Resilience4j retry
        }
    }

    public void handleDeliveryFailure(String url, String eventType, String payload, Exception e) {
        log.error("Permament failure delivering webhook {} to {}: {}", eventType, url, e.getMessage());
        // Here we could publish a failure event or notification
    }
}
