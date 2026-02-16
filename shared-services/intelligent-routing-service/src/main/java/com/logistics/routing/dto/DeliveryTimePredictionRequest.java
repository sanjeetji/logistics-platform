package com.logistics.routing.dto;

import java.util.Map;

public class DeliveryTimePredictionRequest {
    private Double pickupLat;
    private Double pickupLng;
    private Double deliveryLat;
    private Double deliveryLng;
    private String vehicleType;
    private String timeOfDay;
    private String weatherCondition;

    public DeliveryTimePredictionRequest() {
    }

    public DeliveryTimePredictionRequest(Double pickupLat, Double pickupLng, Double deliveryLat, Double deliveryLng,
            String vehicleType, String timeOfDay, String weatherCondition) {
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
        this.vehicleType = vehicleType;
        this.timeOfDay = timeOfDay;
        this.weatherCondition = weatherCondition;
    }

    public static DeliveryTimePredictionRequestBuilder builder() {
        return new DeliveryTimePredictionRequestBuilder();
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public Double getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(Double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public Double getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(Double deliveryLng) {
        this.deliveryLng = deliveryLng;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public static class DeliveryTimePredictionRequestBuilder {
        private Double pickupLat;
        private Double pickupLng;
        private Double deliveryLat;
        private Double deliveryLng;
        private String vehicleType;
        private String timeOfDay;
        private String weatherCondition;

        public DeliveryTimePredictionRequestBuilder pickupLat(Double pickupLat) {
            this.pickupLat = pickupLat;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder pickupLng(Double pickupLng) {
            this.pickupLng = pickupLng;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder deliveryLat(Double deliveryLat) {
            this.deliveryLat = deliveryLat;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder deliveryLng(Double deliveryLng) {
            this.deliveryLng = deliveryLng;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder vehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder timeOfDay(String timeOfDay) {
            this.timeOfDay = timeOfDay;
            return this;
        }

        public DeliveryTimePredictionRequestBuilder weatherCondition(String weatherCondition) {
            this.weatherCondition = weatherCondition;
            return this;
        }

        public DeliveryTimePredictionRequest build() {
            return new DeliveryTimePredictionRequest(pickupLat, pickupLng, deliveryLat, deliveryLng, vehicleType,
                    timeOfDay, weatherCondition);
        }
    }
}
