package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLABreachPredictedEvent implements Serializable {

    private String orderId;
    private String slaId;
    private String slaName;

    private LocalDateTime predictedBreachTime;
    private LocalDateTime currentETA;
    private LocalDateTime requiredETA;

    private Double confidence; // 0.0 to 1.0
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    private String recommendedAction;
    private LocalDateTime timestamp;
}
