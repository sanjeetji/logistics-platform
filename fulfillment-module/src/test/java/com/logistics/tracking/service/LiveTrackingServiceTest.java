package com.logistics.tracking.service;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import com.logistics.routing.ml.ETAPredictionService;
import com.logistics.tracking.dto.LiveTrackingResponse;
import com.logistics.tracking.dto.LocationUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LiveTrackingServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ETAPredictionService etaPredictionService;

    @InjectMocks
    private LiveTrackingService liveTrackingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getInitialTrackingState_withValidData_shouldReturnFullResponse() {
        // Mock Redis Location
        LocationUpdate mockLocation = new LocationUpdate();
        mockLocation.setDriverId("drv-123");
        mockLocation.setLatitude(40.7128);
        mockLocation.setLongitude(-74.0060);
        mockLocation.setTimestamp(LocalDateTime.now());
        when(valueOperations.get("location:ORD-999")).thenReturn(mockLocation);

        // Mock ETA
        ETAPredictionResponse mockEta = new ETAPredictionResponse();
        mockEta.setPredictedDurationSeconds(1800L);
        when(etaPredictionService.predictETA(any(ETAPredictionRequest.class))).thenReturn(mockEta);

        // Call Service
        LiveTrackingResponse response = liveTrackingService.getInitialTrackingState("ORD-999");

        // Asserts
        assertNotNull(response);
        assertEquals("ORD-999", response.getOrderId());
        assertEquals("IN_TRANSIT", response.getOrderStatus());
        assertNotNull(response.getLastKnownLocation());
        assertEquals(40.7128, response.getLastKnownLocation().getLatitude());
        assertNotNull(response.getEtaPrediction());
        assertEquals(1800L, response.getEtaPrediction().getPredictedDurationSeconds());
    }

    @Test
    void getInitialTrackingState_withNoRedisData_shouldReturnDispatchingState() {
        when(valueOperations.get("location:ORD-888")).thenReturn(null);

        ETAPredictionResponse mockEta = new ETAPredictionResponse();
        mockEta.setPredictedDurationSeconds(3600L);
        when(etaPredictionService.predictETA(any(ETAPredictionRequest.class))).thenReturn(mockEta);

        LiveTrackingResponse response = liveTrackingService.getInitialTrackingState("ORD-888");

        assertNotNull(response);
        assertEquals("ORD-888", response.getOrderId());
        assertEquals("DISPATCHING", response.getOrderStatus());
        assertNull(response.getLastKnownLocation());
        assertNotNull(response.getEtaPrediction());
    }
}
