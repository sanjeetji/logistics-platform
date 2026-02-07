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
@EqualsAndHashCode(callSuper = true)
public class CustomerAddress extends BaseEntity {

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 50)
    private String label; // "Home", "Work", "Other"

    @Column(nullable = false, columnDefinition = "text")
    private String address;

    private String landmark;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String city;
    
    private String state;
    
    private String pincode;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}
