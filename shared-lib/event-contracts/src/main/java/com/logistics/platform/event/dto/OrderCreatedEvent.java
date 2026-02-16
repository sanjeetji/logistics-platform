package com.logistics.platform.event.dto;

import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends BaseEvent {
    private String orderId;
    private TransportOrderDto orderDto;

    public static OrderCreatedEvent create(String orderId, TransportOrderDto orderDto) {
        return OrderCreatedEvent.builder()
                .orderId(orderId)
                .orderDto(orderDto)
                .eventType("ORDER_CREATED")
                .build();
    }
}
