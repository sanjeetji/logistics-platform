package com.logistics.parcel.service;

import com.logistics.parcel.client.OrderServiceClient;
import com.logistics.parcel.model.Parcel;
import com.logistics.parcel.repository.ParcelRepository;
import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.utils.audit.Auditable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final PartnerAggregatorService partnerAggregatorService;
    private final OrderServiceClient orderServiceClient;

    /**
     * Create Parcel - Delegates Core creation to Order Service first
     */
    @Transactional
    @Auditable(action = "CREATE_PARCEL", entityType = "PARCEL")
    public Parcel createParcel(Parcel parcel) {
        // 1. Create Core Order Request
        // B2C Parcels are usually "B2C_ON_DEMAND" or we can infer based on input
        CreateOrderRequest coreRequest = CreateOrderRequest.builder()
                .customerId("GUEST_USER") // Or map from parcel.senderName
                .type("B2C_ON_DEMAND")
                // Missing Address/Lat/Lng in Parcel model? Parcel has simple string address.
                // Core expects detailed OrderLocation. We will mock lat/lng or parse if
                // possible.
                // For now, we put raw address in instructions or metadata if structured data is
                // missing.
                .build();

        // Add minimal metadata so we know it's a Parcel
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("senderName", parcel.getSenderName());
        metadata.put("receiverName", parcel.getReceiverName());
        metadata.put("weight", parcel.getWeight());
        coreRequest.setMetadata(metadata);

        // 2. Call Core Order Service (Catch potential failures)
        try {
            OrderDTO coreOrder = orderServiceClient.createOrder(coreRequest);
            log.info("Core Order created for Parcel: {}", coreOrder.getOrderId());
            // Link Parcel to Core Order ID?
            // Parcel entity has 'trackingNumber' which is uuid. Ideally we should use
            // OrderId as trackingId or link them.
            // Let's use the core order ID as the tracking number if possible, or link it.
            // But Parcel entity doesn't have 'orderId' field explicitly, it uses
            // 'trackingNumber'.
            // Strategy: Use Core Order ID as the main tracking identifier.
            parcel.setTrackingNumber(coreOrder.getOrderId());
        } catch (Exception e) {
            log.error(
                    "Failed to create Core Order, falling back to local Parcel only (Legacy mode) or propagating Error",
                    e);
            // In a strict Adapter pattern, we should probably fail.
            // But for resilience during migration, we might proceed.
            // Let's ensure we generate a ID if core fails or we decide to suppress.
            if (parcel.getTrackingNumber() == null) {
                parcel.setTrackingNumber(UUID.randomUUID().toString());
            }
            throw new RuntimeException("Failed to register order with Core Service: " + e.getMessage());
        }

        parcel.setStatus("CREATED");

        // 3. Partner Aggregation logic
        try {
            String partnerTracking = partnerAggregatorService.createShipmentWithBestPartner(parcel);
            parcel.setPartnerTrackingNumber(partnerTracking);
        } catch (Exception e) {
            log.warn("Partner aggregation failed, proceeding with INTERNAL fulfillment", e);
            parcel.setPartnerTrackingNumber("INTERNAL-" + parcel.getTrackingNumber());
        }

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
