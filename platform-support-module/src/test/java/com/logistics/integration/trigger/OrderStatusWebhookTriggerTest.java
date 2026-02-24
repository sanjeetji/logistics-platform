package com.logistics.integration.trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.integration.model.EcommercePlatform;
import com.logistics.integration.model.WebhookConfig;
import com.logistics.integration.model.WebhookEvent;
import com.logistics.integration.repository.WebhookConfigRepository;
import com.logistics.integration.repository.WebhookEventRepository;
import com.logistics.integration.service.WebhookDispatcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderStatusWebhookTriggerTest {

    @Mock
    private WebhookConfigRepository configRepository;

    @Mock
    private WebhookEventRepository eventRepository;

    @Mock
    private WebhookDispatcherService dispatcherService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderStatusWebhookTrigger webhookTrigger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webhookTrigger = new OrderStatusWebhookTrigger(configRepository, eventRepository, dispatcherService,
                objectMapper);
    }

    @Test
    void onOrderStatusChanged_shouldGenerateWebhookEvent_whenTenantHasActiveConfig() {
        String mockKafkaMessage = "{\"orderId\":\"ORD-123\", \"newStatus\":\"DISPATCHED\", \"tenantId\":\"T-456\"}";

        WebhookConfig config = WebhookConfig.builder()
                .tenantId("T-456")
                .platform(EcommercePlatform.SHOPIFY)
                .isActive(true)
                .build();

        when(configRepository.findByTenantId("T-456")).thenReturn(List.of(config));

        webhookTrigger.onOrderStatusChanged(mockKafkaMessage);

        ArgumentCaptor<WebhookEvent> eventCaptor = ArgumentCaptor.forClass(WebhookEvent.class);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        verify(dispatcherService, times(1)).dispatchWebhook(any(WebhookEvent.class));

        WebhookEvent savedEvent = eventCaptor.getValue();
        assertNotNull(savedEvent);
        assertEquals("T-456", savedEvent.getTenantId());
        assertEquals("ORDER_STATUS_CHANGED", savedEvent.getEventType());
        assertEquals(EcommercePlatform.SHOPIFY, savedEvent.getPlatform());
        assertEquals("https://api.shopify.com/webhooks/logistics", savedEvent.getTargetUrl());
    }

    @Test
    void onOrderStatusChanged_shouldNotGenerateWebhookEvent_whenNoTenantId() {
        String mockKafkaMessage = "{\"orderId\":\"ORD-123\", \"newStatus\":\"DISPATCHED\"}";
        webhookTrigger.onOrderStatusChanged(mockKafkaMessage);
        verify(configRepository, never()).findByTenantId(anyString());
        verify(eventRepository, never()).save(any(WebhookEvent.class));
    }
}
