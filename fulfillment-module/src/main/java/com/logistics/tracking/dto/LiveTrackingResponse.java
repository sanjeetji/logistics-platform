package com.logistics.tracking.dto;

import com.logistics.routing.dto.ETAPredictionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveTrackingResponse {
    private String orderId;
    private String orderStatus;
    private LocationUpdate lastKnownLocation;
    private ETAPredictionResponse etaPrediction;
    private String driverName;
    private String driverContact;
    private String vehicleType;
    private String vehicleLicensePlate;
}
