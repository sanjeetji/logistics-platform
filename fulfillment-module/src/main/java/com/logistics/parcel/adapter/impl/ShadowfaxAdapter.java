package com.logistics.parcel.adapter.impl;

import com.logistics.parcel.adapter.PartnerAdapter;
import com.logistics.parcel.model.Parcel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component("shadowfaxAdapter")
public class ShadowfaxAdapter implements PartnerAdapter {

    @Override
    public String getPartnerName() {
        return "SHADOWFAX";
    }

    @Override
    public BigDecimal getRate(Parcel parcel) {
        // Mock rate calculation: $1.0 per chargeable weight unit
        if (parcel.getChargeableWeight() != null) {
            return parcel.getChargeableWeight().multiply(new BigDecimal("1.0"));
        }
        return new BigDecimal("50.00"); // Base rate
    }

    @Override
    public String createShipment(Parcel parcel) {
        // Mock shipment creation
        return "SFX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public String trackShipment(String partnerTrackingNumber) {
        // Mock tracking
        return "IN_TRANSIT";
    }
}
