package com.logistics.sla.service;

import com.logistics.platform.event.dto.SLABreachPredictedEvent;
import com.logistics.sla.model.SLABreachPrediction;
import com.logistics.sla.model.SLAInstance;
import com.logistics.sla.repository.SLABreachPredictionRepository;
import com.logistics.sla.repository.SLAInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAPredictionService {

    private final SLAInstanceRepository slaInstanceRepository;
    private final SLABreachPredictionRepository predictionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Predict SLA breach based on current location and ETA
     * 
     * @param entityId             Order/Parcel ID
     * @param currentLat           Current latitude
     * @param currentLng           Current longitude
     * @param destinationLat       Destination latitude
     * @param destinationLng       Destination longitude
     * @param estimatedTimeMinutes Estimated time to reach destination
     */
    @Transactional
    public void predictBreach(String entityId, String entityType,
            Double currentLat, Double currentLng,
            Double destinationLat, Double destinationLng,
            Integer estimatedTimeMinutes) {

        log.debug("Predicting SLA breach for entity: {}", entityId);

        // Find active SLA instance for this entity
        Optional<SLAInstance> instanceOpt = slaInstanceRepository
                .findByEntityIdAndIsCompletedFalse(entityId);

        if (instanceOpt.isEmpty()) {
            log.debug("No active SLA instance found for entity: {}", entityId);
            return;
        }

        SLAInstance instance = instanceOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime predictedEndTime = now.plusMinutes(estimatedTimeMinutes);

        // Update instance with predicted end time
        instance.setPredictedEndTime(predictedEndTime);

        // Calculate time remaining until SLA deadline
        long elapsedSeconds = Duration.between(instance.getStartTime(), now).getSeconds();

        // Fetch SLA definition (simplified - in real scenario, fetch from
        // SLARepository)
        long maxDurationSeconds = 3600; // Mock: 1 hour SLA
        long remainingSeconds = maxDurationSeconds - elapsedSeconds;
        long predictedDurationSeconds = Duration.between(now, predictedEndTime).getSeconds();

        // Check if predicted time exceeds remaining time
        if (predictedDurationSeconds > remainingSeconds) {
            double breachMargin = (double) (predictedDurationSeconds - remainingSeconds) / remainingSeconds;
            double confidence = calculateConfidence(breachMargin);
            String riskLevel = determineRiskLevel(confidence);

            instance.setRiskLevel(riskLevel);
            slaInstanceRepository.save(instance);

            // Create or update prediction
            SLABreachPrediction prediction = predictionRepository
                    .findBySlaInstanceId(instance.getId().toString())
                    .orElse(SLABreachPrediction.builder()
                            .slaInstanceId(instance.getId().toString())
                            .entityId(entityId)
                            .entityType(entityType)
                            .status(SLABreachPrediction.PredictionStatus.PREDICTED)
                            .build());

            prediction.setPredictedBreachTime(predictedEndTime);
            prediction.setCurrentETA(predictedEndTime);
            prediction.setRequiredETA(instance.getStartTime().plusSeconds(maxDurationSeconds));
            prediction.setConfidence(confidence);
            prediction.setRiskLevel(SLABreachPrediction.RiskLevel.valueOf(riskLevel));
            prediction.setRecommendedAction(generateRecommendation(riskLevel, breachMargin));

            predictionRepository.save(prediction);

            // Publish event for proactive action
            publishBreachPrediction(instance, prediction);

            log.warn("SLA BREACH PREDICTED for entity: {} with confidence: {} and risk: {}",
                    entityId, confidence, riskLevel);
        } else {
            // Update risk to LOW if no breach predicted
            instance.setRiskLevel("LOW");
            slaInstanceRepository.save(instance);
        }
    }

    private double calculateConfidence(double breachMargin) {
        // Simple confidence calculation based on breach margin
        // Higher margin = higher confidence
        if (breachMargin > 1.0)
            return 0.95;
        if (breachMargin > 0.5)
            return 0.80;
        if (breachMargin > 0.2)
            return 0.60;
        return 0.40;
    }

    private String determineRiskLevel(double confidence) {
        if (confidence > 0.80)
            return "CRITICAL";
        if (confidence > 0.50)
            return "HIGH";
        if (confidence > 0.20)
            return "MEDIUM";
        return "LOW";
    }

    private String generateRecommendation(String riskLevel, double breachMargin) {
        switch (riskLevel) {
            case "CRITICAL":
                return "IMMEDIATE RE-DISPATCH REQUIRED - Breach imminent";
            case "HIGH":
                return "Consider re-routing or assigning backup driver";
            case "MEDIUM":
                return "Monitor closely and prepare contingency";
            default:
                return "Continue monitoring";
        }
    }

    private void publishBreachPrediction(SLAInstance instance, SLABreachPrediction prediction) {
        SLABreachPredictedEvent event = SLABreachPredictedEvent.builder()
                .orderId(instance.getEntityId())
                .slaId(instance.getSlaId())
                .slaName("Order Delivery SLA") // Mock
                .predictedBreachTime(prediction.getPredictedBreachTime())
                .currentETA(prediction.getCurrentETA())
                .requiredETA(prediction.getRequiredETA())
                .confidence(prediction.getConfidence())
                .riskLevel(prediction.getRiskLevel().name())
                .recommendedAction(prediction.getRecommendedAction())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("sla.breach.predicted", event);
        log.info("Published SLA breach prediction event for order: {}", instance.getEntityId());
    }
}
