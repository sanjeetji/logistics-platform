package com.logistics.fleet.service;

import com.logistics.fleet.model.Geofence;
import com.logistics.fleet.repository.GeofenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeofenceService {

    private final GeofenceRepository geofenceRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;
    private static final String REDIS_PREFIX = "driver_geofences:";

    public void checkGeofences(Long driverId, Point currentLocation) {
        log.debug("Checking geofences for driver {} at {}", driverId, currentLocation);

        // 1. Find all active geofences containing this point
        List<Geofence> currentGeofences = geofenceRepository.findContainingGeofences(currentLocation);
        Set<Long> currentIds = currentGeofences.stream()
                .map(Geofence::getId)
                .collect(Collectors.toSet());

        // 2. Get previous state from Redis
        String redisKey = REDIS_PREFIX + driverId;
        Set<Long> previousIds = (Set<Long>) redisTemplate.opsForValue().get(redisKey);
        if (previousIds == null) {
            previousIds = new HashSet<>();
        }

        // 3. Detect changes
        Set<Long> entered = new HashSet<>(currentIds);
        entered.removeAll(previousIds);

        Set<Long> exited = new HashSet<>(previousIds);
        exited.removeAll(currentIds);

        // 4. Fire events
        for (Long gid : entered) {
            handleEntry(driverId, gid);
        }

        for (Long gid : exited) {
            handleExit(driverId, gid);
        }

        // 5. Update Redis
        redisTemplate.opsForValue().set(redisKey, currentIds);
    }

    private void handleEntry(Long driverId, Long geofenceId) {
        Geofence geofence = geofenceRepository.findById(geofenceId).orElse(null);
        if (geofence == null)
            return;

        log.info("Driver {} ENTERED geofence {} ({})", driverId, geofenceId, geofence.getName());

        publishEvent(driverId, geofence, com.logistics.platform.event.dto.GeofenceEventType.ENTER);
    }

    private void handleExit(Long driverId, Long geofenceId) {
        Geofence geofence = geofenceRepository.findById(geofenceId).orElse(null);
        if (geofence == null)
            return;

        log.info("Driver {} EXITED geofence {} ({})", driverId, geofenceId, geofence.getName());

        publishEvent(driverId, geofence, com.logistics.platform.event.dto.GeofenceEventType.EXIT);
    }

    private void publishEvent(Long driverId, Geofence geofence,
            com.logistics.platform.event.dto.GeofenceEventType type) {
        com.logistics.platform.event.dto.GeofenceEvent event = com.logistics.platform.event.dto.GeofenceEvent.builder()
                .driverId(driverId.toString())
                .geofenceId(geofence.getId())
                .geofenceName(geofence.getName())
                .eventType(type)
                .associatedEntityId(geofence.getAssociatedEntityId())
                .associatedEntityType(geofence.getAssociatedEntityType())
                .purpose(geofence.getPurpose())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        kafkaTemplate.send("geofence-events", driverId.toString(), event);
    }
}
