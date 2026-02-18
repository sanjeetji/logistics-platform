package com.logistics.bff.unified.service.mobile;

import com.logistics.bff.unified.dto.mobile.OptimizedOrderResponse;
import com.logistics.platform.dto.order.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mobile Payload Optimization Service
 * Optimizes response payloads for mobile devices to save bandwidth
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MobilePayloadOptimizationService {

    /**
     * Optimize response payload for mobile devices
     */
    public List<OptimizedOrderResponse> getOptimizedOrders(List<OrderDTO> orders, int page, int size) {
        log.info("Optimizing orders for mobile: {} count", orders != null ? orders.size() : 0);
        if (orders == null)
            return List.of();

        return orders.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::transformToOptimized)
                .collect(Collectors.toList());
    }

    private OptimizedOrderResponse transformToOptimized(OrderDTO order) {
        return OptimizedOrderResponse.builder()
                .id(order.getOrderId())
                .status(order.getStatus())
                .pickup(truncateAddress(order.getPickupAddress()))
                .drop(truncateAddress(order.getDeliveryAddress()))
                .amount(order.getAmount() != null ? order.getAmount().doubleValue() : 0.0)
                .eta(order.getEstimatedDelivery() != null ? "Today" : "TBD")
                .driverId(order.getDriverId())
                .customerId(order.getCustomerId())
                .coords(List.of(19.0760, 72.8777)) // Mumbai placeholder
                .build();
    }

    private String truncateAddress(String address) {
        if (address == null || address.length() <= 25)
            return address;
        return address.substring(0, 22) + "...";
    }

    /**
     * Compress image URLs to thumbnails for mobile
     */
    public String optimizeImageUrl(String originalUrl) {
        if (originalUrl == null)
            return null;
        return originalUrl; // In real implementation, add CDN flags
    }

    /**
     * Reduce precision of coordinates for bandwidth optimization
     */
    public List<Double> compressCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null)
            return List.of(0.0, 0.0);
        return List.of(
                Math.round(lat * 1000.0) / 1000.0,
                Math.round(lng * 1000.0) / 1000.0);
    }
}
