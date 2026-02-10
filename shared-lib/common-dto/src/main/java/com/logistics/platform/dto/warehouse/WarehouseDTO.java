package com.logistics.platform.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private String id;
    private String name;
    private String code;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private String status; // ACTIVE, INACTIVE, MAINTENANCE
    private Integer capacity;
    private String managerName;
    private String contactNumber;
    private String tenantId;
}
