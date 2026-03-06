package com.logistics.routing.ml;

import com.logistics.routing.dto.RiskPredictionRequest;
import com.logistics.routing.dto.RiskPredictionResponse;
import com.logistics.routing.dto.RiskPredictionResponse.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DelayRiskService {

    public RiskPredictionResponse calculateRisk(RiskPredictionRequest request) {
        double totalRisk = 0.05; // Base inherent risk of 5%
        List<String> factors = new ArrayList<>();

        // 1. Weather Impact
        if (request.getWeatherCondition() != null) {
            String weather = request.getWeatherCondition().toUpperCase();
            switch (weather) {
                case "RAIN":
                    totalRisk += 0.20;
                    factors.add("Wet conditions");
                    break;
                case "SNOW":
                    totalRisk += 0.35;
                    factors.add("Hazardous snow/ice");
                    break;
                case "STORM":
                    totalRisk += 0.50;
                    factors.add("Severe storm warning");
                    break;
                case "FOG":
                    totalRisk += 0.25;
                    factors.add("Low visibility");
                    break;
            }
        }

        // 2. Traffic Impact
        if (request.getTrafficDelayPercent() != null && request.getTrafficDelayPercent() > 0) {
            double traffic = request.getTrafficDelayPercent();
            if (traffic > 50) {
                totalRisk += 0.40;
                factors.add("Severe traffic >50% delay");
            } else if (traffic > 25) {
                totalRisk += 0.25;
                factors.add("Heavy traffic >25% delay");
            } else if (traffic > 10) {
                totalRisk += 0.10;
                factors.add("Moderate traffic");
            }
        }

        // 3. Driver Experience Impact
        if (request.getDriverExperienceLevel() != null) {
            String exp = request.getDriverExperienceLevel().toUpperCase();
            if (exp.equals("BEGINNER")) {
                totalRisk += 0.15;
                factors.add("Inexperienced driver on route");
            } else if (exp.equals("EXPERT")) {
                totalRisk -= 0.10; // Experts mitigate risk
                factors.add("Expert driver mitigation");
            }
        }

        // 4. Distance / Duration Fatigue
        if (request.getEstimatedMinutes() != null && request.getEstimatedMinutes() > 180) {
            totalRisk += 0.15;
            factors.add("Long route driving fatigue risk");
        }

        // Cap risk
        if (totalRisk > 0.99)
            totalRisk = 0.99;
        if (totalRisk < 0.01)
            totalRisk = 0.01;

        // Categorize Risk Level
        RiskLevel level = RiskLevel.LOW;
        if (totalRisk >= 0.75) {
            level = RiskLevel.CRITICAL;
        } else if (totalRisk >= 0.50) {
            level = RiskLevel.HIGH;
        } else if (totalRisk >= 0.25) {
            level = RiskLevel.MEDIUM;
        }

        // Estimate arbitrary delay based on risk intensity and base ETA
        int baseMin = request.getEstimatedMinutes() != null ? request.getEstimatedMinutes().intValue() : 30;
        int estimatedDelay = (int) (baseMin * totalRisk);

        return RiskPredictionResponse.builder()
                .riskProbability(Math.round(totalRisk * 100.0) / 100.0)
                .riskLevel(level)
                .estimatedDelayMinutes(estimatedDelay)
                .contributingFactors(factors)
                .build();
    }
}
