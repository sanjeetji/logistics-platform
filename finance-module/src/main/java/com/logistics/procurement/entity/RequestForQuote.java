package com.logistics.procurement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "request_for_quotes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestForQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rfqId; // e.g. RFQ-001

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String originCountry;

    @Column(nullable = false)
    private String destinationCountry;

    private Double totalWeightKg;
    private Double totalVolumeCbm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RfqStatus status;

    private LocalDateTime bidDeadline;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VendorBid> bids = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum RfqStatus {
        OPEN, EVALUATING, AWARDED, CANCELLED
    }
}
