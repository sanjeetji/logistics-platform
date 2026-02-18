package com.logistics.bff.unified.service.mobile;

import com.logistics.bff.unified.client.OrderServiceClient;
import com.logistics.bff.unified.dto.OptimizedOrderResponse;
import com.logistics.platform.dto.order.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MobilePayloadOptimizationService {

    private final OrderServiceClient orderClient;

    /**
     * Optimize response payload for mobile devices
     * - Reduce field count
     * - Compress nested objects
     * - Use abbreviations
     * - Paginate by default
     */
    public List<OptimizedOrderResponse> getOptimizedOrders(String userId, int page, int size) {
        log.info("Fetching optimized orders for user: {}, page: {}, size: {}", userId, page, size);

        try {
            List<OrderDTO> orders = orderClient.getCustomerOrders(userId);
            if (orders == null)
                return Collections.emptyList();

            // Simple pagination (manual for demonstration)
            return orders.stream()
                    .skip((long) page * size)
                    .limit(size)
                    .map(this::transformToOptimized)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to fetch orders for mobile optimization: {}", userId, e);
            return Collections.emptyList();
        }
    }

    private OptimizedOrderResponse transformToOptimized(OrderDTO order) {
        return OptimizedOrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .pickup(truncateAddress(order.getPickupAddress()))
                .drop(truncateAddress(order.getDeliveryAddress()))
                .amount(order.getAmount() != null ? order.getAmount().doubleValue() : 0.0)
                .eta(order.getEstimatedDelivery() != null ? "Today" : "TBD")
                .driverId(order.getDriverId())
                .customerId(order.getCustomerId())
                .coords(List.of(19.0760, 72.8777)) // Placeholder coords
                .build();
    }

    private String truncateAddress(String address) {
        if (address == null || address.length() <= 20)
            return address;
        return address.substring(0, 17) + "...";
    }

    /**
     * Compress image URLs to thumbnails for mobile
     */
    public String optimizeImageUrl(String originalUrl) {
        // Optimized CDN thumbnail URL
        if (originalUrl == null)
            return null;
        return originalUrl.contains("?") ? originalUrl + "&w=200&h=200&fm=webp" : originalUrl + "?w=200&h=200&fm=webp";
    }

    /**
     * Reduce precision of coordinates for bandwidth optimization
     */
    public List<Double> compressCoordinates(Double lat, Double lng) {
        return List.of(
                Math.round(lat * 10000.0) / 10000.0,
                Math.round(lng * 10000.0) / 10000.0);
    }
}
