package com.logistics.locationhub.service;

import com.logistics.locationhub.dto.LocationUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationOptimizationServiceTest {

    @InjectMocks
    private LocationOptimizationService service;

    @Test
    void calculateAdaptiveFrequency_HighSpeed() {
        int interval = service.calculateAdaptiveFrequency(40.0, 80);
        assertEquals(5, interval);
    }

    @Test
    void calculateAdaptiveFrequency_MediumSpeed() {
        int interval = service.calculateAdaptiveFrequency(20.0, 80);
        assertEquals(10, interval);
    }

    @Test
    void calculateAdaptiveFrequency_Stationary() {
        int interval = service.calculateAdaptiveFrequency(0.0, 80);
        assertEquals(60, interval);
    }

    @Test
    void calculateAdaptiveFrequency_LowBattery() {
        // High speed but low battery -> should double the interval (5 * 2 = 10)
        int interval = service.calculateAdaptiveFrequency(40.0, 10);
        assertEquals(10, interval);
    }

    @Test
    void smoothLocation_InterpolatesCoordinates() {
        LocationUpdateDTO prev = LocationUpdateDTO.builder()
                .latitude(10.0)
                .longitude(10.0)
                .accuracy(10.0)
                .build();

        LocationUpdateDTO raw = LocationUpdateDTO.builder()
                .latitude(20.0)
                .longitude(20.0)
                .accuracy(10.0)
                .build();

        // With equal accuracy, it should be simple average (15.0, 15.0)
        LocationUpdateDTO smooth = service.smoothLocation(raw, prev);

        assertEquals(15.0, smooth.getLatitude());
        assertEquals(15.0, smooth.getLongitude());
    }

    @Test
    void compressTrajectory_ReturnsString() {
        LocationUpdateDTO p1 = LocationUpdateDTO.builder().latitude(10.0).longitude(10.0).build();
        LocationUpdateDTO p2 = LocationUpdateDTO.builder().latitude(20.0).longitude(20.0).build();

        String compressed = service.compressTrajectory(List.of(p1, p2));

        assertTrue(compressed.contains("10.00000,10.00000"));
        assertTrue(compressed.contains("20.00000,20.00000"));
    }
}
