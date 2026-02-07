package com.logistics.order.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLocation {
    private String address;
    private Double latitude;
    private Double longitude;
    private String contactName;
    private String contactPhone;
    private String instructions;
}
