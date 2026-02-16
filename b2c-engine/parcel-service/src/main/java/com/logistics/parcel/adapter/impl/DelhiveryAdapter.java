package com.logistics.parcel.adapter.impl;

import com.logistics.parcel.adapter.PartnerAdapter;
import com.logistics.parcel.model.Parcel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component("delhiveryAdapter")
public class DelhiveryAdapter implements PartnerAdapter {

    @Override
    public String getPartnerName() {
        return "DELHIVERY";
    }

    @Override
    public BigDecimal getRate(Parcel parcel) {
        // Mock rate calculation: $1.2 per chargeable weight unit
        if (parcel.getChargeableWeight() != null) {
            return parcel.getChargeableWeight().multiply(new BigDecimal("1.2"));
        }
        return new BigDecimal("60.00"); // Base rate
    }

    @Override
    public String createShipment(Parcel parcel) {
        // Mock shipment creation
        return "DLV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public String trackShipment(String partnerTrackingNumber) {
        // Mock tracking
        return "PICKED_UP";
    }
}
