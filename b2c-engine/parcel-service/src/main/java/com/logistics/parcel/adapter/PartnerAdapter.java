package com.logistics.parcel.adapter;

import com.logistics.parcel.model.Parcel;
import java.math.BigDecimal;

public interface PartnerAdapter {
    String getPartnerName();

    BigDecimal getRate(Parcel parcel);

    String createShipment(Parcel parcel);

    String trackShipment(String partnerTrackingNumber);
}
