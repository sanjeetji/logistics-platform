package com.logistics.parcel.service;

import com.logistics.parcel.model.Parcel;
import com.logistics.parcel.repository.ParcelRepository;
import com.logistics.platform.utils.audit.Auditable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;

    @Auditable(action = "CREATE_PARCEL", entityType = "PARCEL")
    public Parcel createParcel(Parcel parcel) {
        parcel.setTrackingNumber(UUID.randomUUID().toString());
        parcel.setStatus("CREATED");
        return parcelRepository.save(parcel);
    }

    public Parcel getParcelByTrackingNumber(String trackingNumber) {
        return parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Parcel not found"));
    }

    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll();
    }
}
