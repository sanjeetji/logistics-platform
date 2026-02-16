package com.logistics.routing.traffic;

import com.logistics.routing.dto.TrafficData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Traffic Integration Service
 * 
 * Manages traffic data fetching, caching, and integration with route optimization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficIntegrationService {

    private final GoogleMapsService googleMapsService;
    private final RedisTemplate<String, TrafficData> redisTemplate;

    private static final String TRAFFIC_CACHE_PREFIX = "traffic:";
    private static final long CACHE_TTL_MINUTES = 10;

    /**
     * Get traffic-aware distance and duration with caching
     */
    public TrafficData getTrafficAwareDistance(double originLat, double originLon, 
                                               double destLat, double destLon) {
        
        // Try cache first
        String cacheKey = buildCacheKey(originLat, originLon, destLat, destLon);
        TrafficData cachedData = redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            log.debug("Traffic data found in cache: {}", cacheKey);
            cachedData.setSource("CACHE");
            return cachedData;
        }

        // Fetch from Google Maps
        TrafficData trafficData = googleMapsService.getTrafficData(originLat, originLon, destLat, destLon);
        
        // Cache the result
        if (trafficData != null) {
            redisTemplate.opsForValue().set(cacheKey, trafficData, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Traffic data cached: {}", cacheKey);
        }
        
        return trafficData;
    }

    /**
     * Build cache key for traffic data
     */
    private String buildCacheKey(double originLat, double originLon, double destLat, double destLon) {
        // Round to 4 decimal places (~11m precision) for cache key
        return String.format("%s%.4f_%.4f_%.4f_%.4f", 
            TRAFFIC_CACHE_PREFIX,
            originLat, originLon, destLat, destLon);
    }

    /**
     * Invalidate traffic cache for a specific route segment
     */
    public void invalidateCache(double originLat, double originLon, double destLat, double destLon) {
        String cacheKey = buildCacheKey(originLat, originLon, destLat, destLon);
        redisTemplate.delete(cacheKey);
        log.info("Invalidated traffic cache: {}", cacheKey);
    }

    /**
     * Clear all traffic cache
     */
    public void clearAllCache() {
        redisTemplate.keys(TRAFFIC_CACHE_PREFIX + "*").forEach(key -> redisTemplate.delete(key));
        log.info("Cleared all traffic cache");
    }
}
