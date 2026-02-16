package com.logistics.routing.dto;

import java.util.Map;

public class DeliveryTimePredictionResponse {
    private Integer predictedTimeMinutes;
    private Double confidence;
    private Map<String, Object> factors;

    public DeliveryTimePredictionResponse() {
    }

    public DeliveryTimePredictionResponse(Integer predictedTimeMinutes, Double confidence,
            Map<String, Object> factors) {
        this.predictedTimeMinutes = predictedTimeMinutes;
        this.confidence = confidence;
        this.factors = factors;
    }

    public static DeliveryTimePredictionResponseBuilder builder() {
        return new DeliveryTimePredictionResponseBuilder();
    }

    public Integer getPredictedTimeMinutes() {
        return predictedTimeMinutes;
    }

    public void setPredictedTimeMinutes(Integer predictedTimeMinutes) {
        this.predictedTimeMinutes = predictedTimeMinutes;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Map<String, Object> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Object> factors) {
        this.factors = factors;
    }

    public static class DeliveryTimePredictionResponseBuilder {
        private Integer predictedTimeMinutes;
        private Double confidence;
        private Map<String, Object> factors;

        public DeliveryTimePredictionResponseBuilder predictedTimeMinutes(Integer predictedTimeMinutes) {
            this.predictedTimeMinutes = predictedTimeMinutes;
            return this;
        }

        public DeliveryTimePredictionResponseBuilder confidence(Double confidence) {
            this.confidence = confidence;
            return this;
        }

        public DeliveryTimePredictionResponseBuilder factors(Map<String, Object> factors) {
            this.factors = factors;
            return this;
        }

        public DeliveryTimePredictionResponse build() {
            return new DeliveryTimePredictionResponse(predictedTimeMinutes, confidence, factors);
        }
    }
}
