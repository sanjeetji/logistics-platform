package com.logistics.bff.unified.client.b2c;

import com.logistics.platform.common.dto.parcel.ParcelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "parcel-service")
public interface ParcelClient {
    @PostMapping("/api/v1/parcels")
    ParcelDTO createParcel(@RequestBody ParcelDTO parcel);

    @GetMapping("/api/v1/parcels/{trackingNumber}")
    ParcelDTO getParcelByTracking(@PathVariable("trackingNumber") String trackingNumber);

    @GetMapping("/api/v1/parcels")
    List<ParcelDTO> listParcels();
}
