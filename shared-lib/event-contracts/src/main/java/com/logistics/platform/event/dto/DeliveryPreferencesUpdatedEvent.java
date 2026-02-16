package com.logistics.platform.event.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryPreferencesUpdatedEvent extends BaseEvent {

    private String orderId;
    private String deliveryInstructions;
    private Boolean contactlessDelivery;
    private LocalDateTime preferredDeliveryTimeStart;
    private LocalDateTime preferredDeliveryTimeEnd;
    private String safeDropLocation;

    @Builder(builderMethodName = "deliveryPreferencesBuilder")
    public DeliveryPreferencesUpdatedEvent(String orderId, String deliveryInstructions,
            Boolean contactlessDelivery, LocalDateTime preferredDeliveryTimeStart,
            LocalDateTime preferredDeliveryTimeEnd, String safeDropLocation) {
        this.orderId = orderId;
        this.deliveryInstructions = deliveryInstructions;
        this.contactlessDelivery = contactlessDelivery;
        this.preferredDeliveryTimeStart = preferredDeliveryTimeStart;
        this.preferredDeliveryTimeEnd = preferredDeliveryTimeEnd;
        this.safeDropLocation = safeDropLocation;
        this.setEventType("DELIVERY_PREFERENCES_UPDATED");
    }

    public static DeliveryPreferencesUpdatedEvent create(String orderId, String deliveryInstructions,
            Boolean contactlessDelivery, LocalDateTime preferredDeliveryTimeStart,
            LocalDateTime preferredDeliveryTimeEnd, String safeDropLocation) {
        return deliveryPreferencesBuilder()
                .orderId(orderId)
                .deliveryInstructions(deliveryInstructions)
                .contactlessDelivery(contactlessDelivery)
                .preferredDeliveryTimeStart(preferredDeliveryTimeStart)
                .preferredDeliveryTimeEnd(preferredDeliveryTimeEnd)
                .safeDropLocation(safeDropLocation)
                .build();
    }
}
