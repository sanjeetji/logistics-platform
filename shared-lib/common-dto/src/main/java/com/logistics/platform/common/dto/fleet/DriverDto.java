package com.logistics.platform.common.dto.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String status;
    private String verificationStatus;
    private Double currentLatitude;
    private Double currentLongitude;
    private String vehicleType;
    private Double performanceScore;
    private LocalDateTime shiftStartTime;
    private Integer totalDrivingMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
