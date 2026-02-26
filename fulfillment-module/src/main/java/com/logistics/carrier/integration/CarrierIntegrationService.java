package com.logistics.carrier.integration;

import com.logistics.order.model.Order;
import java.math.BigDecimal;

/**
 * Common abstraction for all 3rd Party Multi-Carrier Integrations (EasyPost,
 * Project44, FedEx, DHL, etc.)
 */
public interface CarrierIntegrationService {

    /**
     * @return the unique identifier for this carrier strategy (e.g., "FEDEX",
     *         "EASYPOST")
     */
    String getCarrierCode();

    /**
     * Gets a live rate quote from the carrier.
     */
    BigDecimal fetchLiveRate(Order order);

    /**
     * Submits the order to the 3rd party to generate labels and a tracking number.
     * 
     * @return tracking number
     */
    String createShipmentAndGetTracking(Order order);

    /**
     * Cancels a previously created shipment.
     */
    boolean cancelShipment(String trackingNumber);
}
