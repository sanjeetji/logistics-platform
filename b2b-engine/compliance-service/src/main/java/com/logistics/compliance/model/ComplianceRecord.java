package com.logistics.compliance.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ComplianceRecord extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String recordId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String complianceType; // CUSTOMS, HAZMAT, TEMPERATURE_CONTROL, etc.

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ComplianceStatus status = ComplianceStatus.PENDING_REVIEW;

    @Column(columnDefinition = "text")
    private String requirements;

    @Column(columnDefinition = "text")
    private String evidence; // JSON or text describing compliance evidence

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(columnDefinition = "text")
    private String nonComplianceReason;
}
