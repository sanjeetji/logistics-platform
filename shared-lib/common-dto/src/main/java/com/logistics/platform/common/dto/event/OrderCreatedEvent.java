package com.logistics.platform.common.dto.event;

import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String eventId;
    private String orderId;
    private TransportOrderDto orderDto;
    private LocalDateTime timestamp;
}
