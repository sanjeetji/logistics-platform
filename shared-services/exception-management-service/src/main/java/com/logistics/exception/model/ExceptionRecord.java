package com.logistics.exception.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "exception_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String exceptionId; // From the event

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String exceptionType;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String severity; // INFO, WARN, CRITICAL

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> metadata;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExceptionStatus status = ExceptionStatus.OPEN;

    private String resolvedBy;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    private LocalDateTime resolvedAt;

    public enum ExceptionStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }
}
