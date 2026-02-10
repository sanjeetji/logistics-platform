package com.logistics.customer.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress extends BaseEntity {

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String label; // "Home", "Office", "Warehouse"

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    // Geocoded coordinates
    private Double latitude;
    private Double longitude;

    @Builder.Default
    private Boolean geocoded = false;

    @Builder.Default
    private Boolean isDefault = false;

    private String contactName;
    private String contactPhone;
}
