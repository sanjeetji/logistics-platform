package com.logistics.parcel.controller;

import com.logistics.parcel.model.Parcel;
import com.logistics.parcel.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    public ResponseEntity<Parcel> createParcel(@RequestBody Parcel parcel) {
        return ResponseEntity.ok(parcelService.createParcel(parcel));
    }

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<Parcel> getParcel(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(parcelService.getParcelByTrackingNumber(trackingNumber));
    }

    @GetMapping
    public ResponseEntity<List<Parcel>> getAllParcels() {
        return ResponseEntity.ok(parcelService.getAllParcels());
    }
}
