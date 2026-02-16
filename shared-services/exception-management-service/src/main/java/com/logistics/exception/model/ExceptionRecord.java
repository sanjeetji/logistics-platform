package com.logistics.exception.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exception_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private Long driverId; // Nullable if order-level exception
    
    private String type; // SLA_BREACH, VEHICLE_BREAKDOWN, etc.
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    
    @Column(length = 1000)
    private String description;
    
    private LocalDateTime timestamp;
    
    @Builder.Default
    private String status = "OPEN"; // OPEN, ACKNOWLEDGED, RESOLVED
}
