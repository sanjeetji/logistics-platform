package com.logistics.sla.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_breach_predictions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLABreachPrediction extends BaseEntity {

    private String slaInstanceId;
    private String entityId; // Order ID or Parcel ID
    private String entityType;

    private LocalDateTime predictedBreachTime;
    private LocalDateTime currentETA;
    private LocalDateTime requiredETA;

    private Double confidence; // 0.0 to 1.0

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private PredictionStatus status;

    private String recommendedAction;
    private boolean actionTaken;
    private LocalDateTime actionTakenAt;

    public enum RiskLevel {
        LOW, // < 20% chance of breach
        MEDIUM, // 20-50% chance
        HIGH, // 50-80% chance
        CRITICAL // > 80% chance
    }

    public enum PredictionStatus {
        PREDICTED, // Initial prediction
        CONFIRMED, // Breach actually occurred
        AVOIDED, // Proactive action prevented breach
        FALSE_ALARM // Prediction was incorrect
    }
}
