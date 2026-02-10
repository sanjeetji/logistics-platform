package com.logistics.analytics.service;

import com.logistics.analytics.dto.EventMessage;
import com.logistics.analytics.model.AnalyticsEvent;
import com.logistics.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for event ingestion
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventIngestionService {

    private final AnalyticsEventRepository eventRepository;

    /**
     * Ingest analytics event
     */
    @Transactional
    public AnalyticsEvent ingestEvent(EventMessage eventMessage) {
        log.debug("Ingesting event: {}", eventMessage);

        // Generate aggregation key (date-based)
        String aggregationKey = eventMessage.getTimestamp() != null 
                ? eventMessage.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventId("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .eventType(eventMessage.getEventType())
                .entityId(eventMessage.getEntityId())
                .entityType(eventMessage.getEntityType())
                .timestamp(eventMessage.getTimestamp() != null ? eventMessage.getTimestamp() : LocalDateTime.now())
                .metadata(eventMessage.getMetadata())
                .aggregationKey(aggregationKey)
                .build();

        return eventRepository.save(event);
    }
}
