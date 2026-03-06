package com.logistics.procurement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_bids")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RequestForQuote rfq;

    @Column(nullable = false)
    private String vendorId;

    @Column(nullable = false)
    private String vendorName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal bidAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    private Integer estimatedTransitTimeDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum BidStatus {
        SUBMITTED, ACCEPTED, REJECTED
    }
}
