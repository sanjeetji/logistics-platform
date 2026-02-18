package com.logistics.geo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceRequest {
    private GeoCoordinates origin;
    private GeoCoordinates destination;
}
