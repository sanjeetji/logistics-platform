package com.logistics.locationhub.service;

import com.logistics.locationhub.dto.LocationUpdateDTO;
import com.logistics.platform.event.dto.DriverLocationUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationIngestionServiceTest {

    @Mock
    private RedisOperations<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private KafkaOperations<String, Object> kafkaTemplate;

    private LocationIngestionService service;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new LocationIngestionService(redisTemplate, kafkaTemplate);
    }

    @Test
    void shouldIngest_FirstLocation() {
        LocationUpdateDTO update = createUpdate("d1", 10.0, 10.0, LocalDateTime.now());

        when(valueOperations.get(anyString())).thenReturn(null);

        service.ingestLocation(update);

        verify(kafkaTemplate).send(eq("driver-location-updates"), eq("d1"), any(DriverLocationUpdatedEvent.class));
        verify(valueOperations).set(anyString(), eq(update));
    }

    @Test
    void shouldSkip_Jitter_SmallDistance() {
        LocalDateTime now = LocalDateTime.now();
        LocationUpdateDTO last = createUpdate("d1", 10.0, 10.0, now.minusSeconds(5));
        // Very small change in location
        LocationUpdateDTO current = createUpdate("d1", 10.00001, 10.00001, now);

        when(valueOperations.get(anyString())).thenReturn(last);

        service.ingestLocation(current);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(valueOperations, never()).set(anyString(), any());
    }

    @Test
    void shouldSkip_Throttle_ReasonableDistanceButTooSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocationUpdateDTO last = createUpdate("d1", 10.0, 10.0, now.minusSeconds(1));
        // Moved 50 meters (significant) but only 1 second passed -> Throttle
        // 0.00045 degrees is approx 50m
        LocationUpdateDTO current = createUpdate("d1", 10.00045, 10.00045, now);

        when(valueOperations.get(anyString())).thenReturn(last);

        service.ingestLocation(current);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    void shouldIngest_ValidUpdate() {
        LocalDateTime now = LocalDateTime.now();
        LocationUpdateDTO last = createUpdate("d1", 10.0, 10.0, now.minusSeconds(5));
        // Moved significantly and enough time passed
        LocationUpdateDTO current = createUpdate("d1", 10.001, 10.001, now);

        when(valueOperations.get(anyString())).thenReturn(last);

        service.ingestLocation(current);

        ArgumentCaptor<DriverLocationUpdatedEvent> captor = ArgumentCaptor.forClass(DriverLocationUpdatedEvent.class);
        verify(kafkaTemplate).send(eq("driver-location-updates"), eq("d1"), captor.capture());

        DriverLocationUpdatedEvent event = captor.getValue();
        assertEquals("d1", event.getDriverId());
        assertEquals(10.001, event.getLatitude());
        assertNotNull(event.getEventId()); // From BaseEvent
    }

    private LocationUpdateDTO createUpdate(String driverId, double lat, double lon, LocalDateTime time) {
        return LocationUpdateDTO.builder()
                .driverId(driverId)
                .latitude(lat)
                .longitude(lon)
                .timestamp(time)
                .accuracy(5.0)
                .speed(10.0)
                .bearing(90.0)
                .build();
    }
}
