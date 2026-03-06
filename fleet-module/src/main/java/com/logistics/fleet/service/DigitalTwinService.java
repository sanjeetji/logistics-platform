package com.logistics.fleet.service;

import com.logistics.fleet.model.Vehicle;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalTwinService {

    /**
     * Aggregates live telemetry and returns a 3D coordinate representation of the
     * physical network.
     * This allows rendering the logistics network in Unreal Engine or WebGL.
     */
    public DigitalTwinSnapshot getNetworkSnapshot() {
        log.info("Generating real-time 3D Digital Twin Snapshot.");

        List<TwinEntity> entities = new ArrayList<>();

        // Mock rendering of the digital twin
        entities.add(TwinEntity.builder()
                .entityId("WH-001")
                .type("WAREHOUSE")
                .coordX(104.5)
                .coordY(20.3)
                .coordZ(0.0) // Ground level
                .status("IDLE")
                .build());

        entities.add(TwinEntity.builder()
                .entityId(UUID.randomUUID().toString())
                .type("TRUCK")
                .coordX(102.1)
                .coordY(19.5)
                .coordZ(0.0)
                .status("IN_TRANSIT")
                .speedMps(22.5) // meters per second
                .headingDegrees(45.0)
                .build());

        return DigitalTwinSnapshot.builder()
                .snapshotId(UUID.randomUUID().toString())
                .timestamp(java.time.Instant.now().toEpochMilli())
                .entities(entities)
                .build();
    }

    @Data
    @Builder
    public static class DigitalTwinSnapshot {
        private final String snapshotId;
        private final Long timestamp;
        private final List<TwinEntity> entities;
    }

    @Data
    @Builder
    public static class TwinEntity {
        private final String entityId;
        private final String type; // e.g., WAREHOUSE, TRUCK, VAN, DRONE
        private final Double coordX;
        private final Double coordY;
        private final Double coordZ;
        private final String status;
        private final Double speedMps;
        private final Double headingDegrees;
    }
}
