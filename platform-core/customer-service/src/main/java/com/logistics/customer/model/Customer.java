package com.logistics.customer.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private Long userId; // Link to auth-service User

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // Default address stored as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> defaultAddress;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Column(precision = 3, scale = 2)
    private Double rating; // Average rating (0.00 to 5.00)

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(columnDefinition = "text")
    private String notes;
}
