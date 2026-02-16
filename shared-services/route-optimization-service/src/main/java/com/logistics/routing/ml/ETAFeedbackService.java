package com.logistics.routing.ml;

import com.logistics.routing.kafka.ETAFeedbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * ETA Feedback Service
 * 
 * Sends actual vs predicted ETA data to ML service for model improvement
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ETAFeedbackService {

    private final KafkaTemplate<String, ETAFeedbackEvent> kafkaTemplate;

    private static final String FEEDBACK_TOPIC = "eta-feedback";

    /**
     * Send ETA feedback to ML service
     */
    public void sendFeedback(ETAFeedbackEvent feedback) {
        
        // Calculate error metrics
        if (feedback.getPredictedDurationSeconds() != null && feedback.getActualDurationSeconds() != null) {
            long error = feedback.getActualDurationSeconds() - feedback.getPredictedDurationSeconds();
            feedback.setErrorSeconds(error);
            
            double errorPercentage = (Math.abs(error) * 100.0) / feedback.getPredictedDurationSeconds();
            feedback.setErrorPercentage(errorPercentage);
            
            log.info("ETA Feedback: route={}, predicted={}s, actual={}s, error={}s ({}%)",
                feedback.getRouteId(),
                feedback.getPredictedDurationSeconds(),
                feedback.getActualDurationSeconds(),
                error,
                String.format("%.1f", errorPercentage));
        }
        
        feedback.setTimestamp(System.currentTimeMillis());
        
        try {
            kafkaTemplate.send(FEEDBACK_TOPIC, feedback.getRouteId(), feedback);
            log.debug("ETA feedback sent to Kafka: {}", feedback.getFeedbackId());
        } catch (Exception e) {
            log.error("Failed to send ETA feedback to Kafka", e);
        }
    }
}
