package com.logistics.tracking.service;

import com.logistics.routing.dto.ETAPredictionRequest;
import com.logistics.routing.dto.ETAPredictionResponse;
import com.logistics.routing.ml.ETAPredictionService;
import com.logistics.tracking.dto.LiveTrackingResponse;
import com.logistics.tracking.dto.LocationUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveTrackingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ETAPredictionService etaPredictionService;

    public LiveTrackingResponse getInitialTrackingState(String orderId) {
        log.info("Fetching initial live tracking state for order: {}", orderId);

        // 1. Fetch live Redis Location state
        Object locationObj = redisTemplate.opsForValue().get("location:" + orderId);
        LocationUpdate lastKnownLocation = null;
        if (locationObj instanceof LocationUpdate) {
            lastKnownLocation = (LocationUpdate) locationObj;
        }

        // 2. Predict ETA (Simulated external lookup using base request)
        ETAPredictionRequest etaRequest = new ETAPredictionRequest();
        etaRequest.setOrderId(orderId);
        etaRequest.setTrafficDelayPercent(12.0); // Mock traffic
        etaRequest.setWeatherCondition("CLEAR");
        ETAPredictionResponse etaPrediction = null;
        try {
            etaPrediction = etaPredictionService.predictETA(etaRequest);
        } catch (Exception e) {
            log.warn("Could not calculate ETA for order: {}", orderId, e);
        }

        // 3. Complete DTO compilation
        return LiveTrackingResponse.builder()
                .orderId(orderId)
                .orderStatus(lastKnownLocation != null ? "IN_TRANSIT" : "DISPATCHING")
                .lastKnownLocation(lastKnownLocation)
                .etaPrediction(etaPrediction)
                .driverName(lastKnownLocation != null ? "Assigned Driver" : "TBD") // Resolves from identity in real
                                                                                   // scenario
                .vehicleType("VAN")
                .build();
    }
}
