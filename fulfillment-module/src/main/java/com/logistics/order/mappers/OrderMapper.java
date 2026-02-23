package com.logistics.order.mappers;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderLocation;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderType;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    public TransportOrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        TransportOrderDto dto = new TransportOrderDto();
        dto.setId(order.getId());
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomerId());
        dto.setTenantId(order.getTenantId());

        if (order.getType() != null) {
            dto.setOrderType(order.getType().name());
        }
        if (order.getStatus() != null) {
            dto.setStatus(order.getStatus().name());
        }

        dto.setWeightKg(order.getWeightKg());

        if (order.getPrice() != null) {
            dto.setPrice(order.getPrice().doubleValue());
        }

        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getPickupLocation() != null) {
            dto.setPickupAddress(order.getPickupLocation().getAddress());
            dto.setPickupLat(order.getPickupLocation().getLatitude());
            dto.setPickupLng(order.getPickupLocation().getLongitude());
        }

        if (order.getDropLocation() != null) {
            dto.setDropAddress(order.getDropLocation().getAddress());
            dto.setDropLat(order.getDropLocation().getLatitude());
            dto.setDropLng(order.getDropLocation().getLongitude());
        }

        return dto;
    }

    public Order toEntity(TransportOrderDto dto) {
        if (dto == null) {
            return null;
        }

        var builder = Order.builder();

        // BaseEntity fields like id, createdAt usually not set from DTO on creation,
        // but maybe on update
        builder.orderId(dto.getOrderId());
        builder.customerId(dto.getCustomerId());
        builder.tenantId(dto.getTenantId());

        if (dto.getOrderType() != null) {
            try {
                builder.type(OrderType.valueOf(dto.getOrderType()));
            } catch (IllegalArgumentException e) {
                // Handle or ignore
            }
        }

        if (dto.getStatus() != null) {
            try {
                builder.status(OrderStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Handle or ignore
            }
        }

        builder.weightKg(dto.getWeightKg());

        if (dto.getPrice() != null) {
            builder.price(BigDecimal.valueOf(dto.getPrice()));
        }

        OrderLocation pickup = new OrderLocation();
        pickup.setAddress(dto.getPickupAddress());
        pickup.setLatitude(dto.getPickupLat());
        pickup.setLongitude(dto.getPickupLng());
        builder.pickupLocation(pickup);

        OrderLocation drop = new OrderLocation();
        drop.setAddress(dto.getDropAddress());
        drop.setLatitude(dto.getDropLat());
        drop.setLongitude(dto.getDropLng());
        builder.dropLocation(drop);

        return builder.build();
    }
}
