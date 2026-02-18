package com.logistics.marketplace.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class DemandPredictionService {

    private final Random random = new Random();

    public Map<String, Integer> predictDemand(LocalDate date) {
        // Mock prediction logic
        // In real system, this would call Python ML service or load pre-calculated stats
        Map<String, Integer> zonePredictions = new HashMap<>();
        
        // Simulate predictions for a few standard zones
        zonePredictions.put("ZONE-A", 50 + random.nextInt(50));
        zonePredictions.put("ZONE-B", 30 + random.nextInt(30));
        zonePredictions.put("ZONE-C", 100 + random.nextInt(100));
        
        // Adjust based on day of week (weekend higher)
        if (date.getDayOfWeek().getValue() >= 6) {
            zonePredictions.replaceAll((k, v) -> v + 20);
        }

        return zonePredictions;
    }
}
