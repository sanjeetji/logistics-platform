package com.logistics.exception.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.exception.dto.TrackingEventDto;
import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.repository.ExceptionRepository;
import com.logistics.platform.event.dto.ExceptionCreatedEvent;
import com.logistics.platform.event.dto.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ExceptionEventListenerTest {

    @Mock
    private ExceptionRepository exceptionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ExceptionEventListener exceptionEventListener;

    @BeforeEach
    void setUp() {
        // Registering JavaTimeModule to handle LocalDateTime
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        exceptionEventListener = new ExceptionEventListener(exceptionRepository, objectMapper, kafkaTemplate);
    }

    @Test
    void testListen_SlaBreach() throws Exception {
        // Arrange
        TrackingEventDto eventDto = TrackingEventDto.builder()
                .orderId("ORD-123")
                .driverId(101L)
                .eventType("SLA_BREACH_PREDICTED")
                .message("Delivery likely to be delayed")
                .timestamp(LocalDateTime.now())
                .build();

        String message = objectMapper.writeValueAsString(eventDto);

        ExceptionRecord savedRecord = ExceptionRecord.builder()
                .id(1L)
                .orderId("ORD-123")
                .driverId(101L)
                .type("SLA_BREACH_PREDICTED")
                .severity("HIGH")
                .description("Delivery likely to be delayed")
                .timestamp(eventDto.getTimestamp())
                .status("OPEN")
                .build();

        when(exceptionRepository.save(any(ExceptionRecord.class))).thenReturn(savedRecord);

        // Act
        exceptionEventListener.listen(message);

        // Assert
        verify(exceptionRepository).save(any(ExceptionRecord.class));

        ArgumentCaptor<ExceptionCreatedEvent> exceptionEventCaptor = ArgumentCaptor
                .forClass(ExceptionCreatedEvent.class);
        verify(kafkaTemplate).send(eq("exception.events"), exceptionEventCaptor.capture());
        ExceptionCreatedEvent capturedExceptionEvent = exceptionEventCaptor.getValue();
        assertEquals("ORD-123", capturedExceptionEvent.getOrderId());
        assertEquals("SLA_BREACH_PREDICTED", capturedExceptionEvent.getType());

        ArgumentCaptor<NotificationEvent> notificationEventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(kafkaTemplate).send(eq("notification.events"), notificationEventCaptor.capture());
        NotificationEvent capturedNotificationEvent = notificationEventCaptor.getValue();
        assertEquals("OPS_MANAGER", capturedNotificationEvent.getRecipient());
        assertEquals("PUSH", capturedNotificationEvent.getType());
        assertNotNull(capturedNotificationEvent.getMetaData());
        assertEquals("ORD-123", capturedNotificationEvent.getMetaData().get("orderId"));
    }

    @Test
    void testListen_NonSlaBreach() throws Exception {
        // Arrange
        TrackingEventDto eventDto = TrackingEventDto.builder()
                .orderId("ORD-123")
                .eventType("LOCATION_UPDATE")
                .build();

        String message = objectMapper.writeValueAsString(eventDto);

        // Act
        exceptionEventListener.listen(message);

        // Assert
        verify(exceptionRepository, times(0)).save(any(ExceptionRecord.class));
        verify(kafkaTemplate, times(0)).send(any(String.class), any(Object.class));
    }
}
