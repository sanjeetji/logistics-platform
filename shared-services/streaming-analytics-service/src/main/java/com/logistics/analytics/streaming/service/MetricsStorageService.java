package com.logistics.analytics.streaming.service;

import com.logistics.analytics.streaming.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsStorageService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ORDER_METRICS_KEY = "metrics:orders:latest";
    private static final String DRIVER_METRICS_KEY = "metrics:drivers:latest";
    private static final String REVENUE_METRICS_KEY = "metrics:revenue:latest";
    private static final String SLA_METRICS_KEY = "metrics:sla:latest";
    private static final String ALERTS_KEY_PREFIX = "alerts:";
    
    private static final long METRICS_TTL_HOURS = 24;
    
    /**
     * Store order metrics in Redis
     */
    public void storeOrderMetrics(OrderMetrics metrics) {
        try {
            redisTemplate.opsForValue().set(
                ORDER_METRICS_KEY,
                metrics,
                METRICS_TTL_HOURS,
                TimeUnit.HOURS
            );
            log.debug("Stored order metrics in Redis");
        } catch (Exception e) {
            log.error("Error storing order metrics", e);
        }
    }
    
    /**
     * Get latest order metrics
     */
    public OrderMetrics getOrderMetrics() {
        try {
            Object value = redisTemplate.opsForValue().get(ORDER_METRICS_KEY);
            if (value != null) {
                return objectMapper.convertValue(value, OrderMetrics.class);
            }
        } catch (Exception e) {
            log.error("Error retrieving order metrics", e);
        }
        return OrderMetrics.builder().build();
    }
    
    /**
     * Store driver metrics in Redis
     */
    public void storeDriverMetrics(DriverMetrics metrics) {
        try {
            redisTemplate.opsForValue().set(
                DRIVER_METRICS_KEY,
                metrics,
                METRICS_TTL_HOURS,
                TimeUnit.HOURS
            );
            log.debug("Stored driver metrics in Redis");
        } catch (Exception e) {
            log.error("Error storing driver metrics", e);
        }
    }
    
    /**
     * Get latest driver metrics
     */
    public DriverMetrics getDriverMetrics() {
        try {
            Object value = redisTemplate.opsForValue().get(DRIVER_METRICS_KEY);
            if (value != null) {
                return objectMapper.convertValue(value, DriverMetrics.class);
            }
        } catch (Exception e) {
            log.error("Error retrieving driver metrics", e);
        }
        return DriverMetrics.builder().build();
    }
    
    /**
     * Store revenue metrics in Redis
     */
    public void storeRevenueMetrics(RevenueMetrics metrics) {
        try {
            redisTemplate.opsForValue().set(
                REVENUE_METRICS_KEY,
                metrics,
                METRICS_TTL_HOURS,
                TimeUnit.HOURS
            );
            log.debug("Stored revenue metrics in Redis");
        } catch (Exception e) {
            log.error("Error storing revenue metrics", e);
        }
    }
    
    /**
     * Get latest revenue metrics
     */
    public RevenueMetrics getRevenueMetrics() {
        try {
            Object value = redisTemplate.opsForValue().get(REVENUE_METRICS_KEY);
            if (value != null) {
                return objectMapper.convertValue(value, RevenueMetrics.class);
            }
        } catch (Exception e) {
            log.error("Error retrieving revenue metrics", e);
        }
        return RevenueMetrics.builder().build();
    }
    
    /**
     * Store SLA metrics in Redis
     */
    public void storeSLAMetrics(SLAMetrics metrics) {
        try {
            redisTemplate.opsForValue().set(
                SLA_METRICS_KEY,
                metrics,
                METRICS_TTL_HOURS,
                TimeUnit.HOURS
            );
            log.debug("Stored SLA metrics in Redis");
        } catch (Exception e) {
            log.error("Error storing SLA metrics", e);
        }
    }
    
    /**
     * Get latest SLA metrics
     */
    public SLAMetrics getSLAMetrics() {
        try {
            Object value = redisTemplate.opsForValue().get(SLA_METRICS_KEY);
            if (value != null) {
                return objectMapper.convertValue(value, SLAMetrics.class);
            }
        } catch (Exception e) {
            log.error("Error retrieving SLA metrics", e);
        }
        return SLAMetrics.builder().build();
    }
    
    /**
     * Store anomaly alert
     */
    public void storeAlert(AnomalyAlert alert) {
        try {
            String key = ALERTS_KEY_PREFIX + alert.getAlertId();
            redisTemplate.opsForValue().set(
                key,
                alert,
                METRICS_TTL_HOURS,
                TimeUnit.HOURS
            );
            
            // Add to alerts set
            redisTemplate.opsForSet().add("alerts:active", alert.getAlertId());
            
            log.info("Stored anomaly alert: {}", alert.getAlertId());
        } catch (Exception e) {
            log.error("Error storing alert", e);
        }
    }
}
