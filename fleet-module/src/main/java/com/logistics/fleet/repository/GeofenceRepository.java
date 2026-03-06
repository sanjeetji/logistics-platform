package com.logistics.fleet.repository;

import com.logistics.fleet.model.Geofence;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    @Query(value = "SELECT * FROM geofences g WHERE g.is_active = true AND ST_Within(:point, g.boundary)", nativeQuery = true)
    List<Geofence> findContainingGeofences(@Param("point") Point point);

    @Query(value = "SELECT * FROM geofences g WHERE g.is_active = true AND g.associated_entity_type = :entityType AND g.associated_entity_id = :entityId", nativeQuery = true)
    List<Geofence> findByAssociatedEntity(@Param("entityType") String entityType, @Param("entityId") String entityId);

    @Query(value = "SELECT * FROM geofences g WHERE g.is_active = true AND g.type = 'SERVICE_ZONE' AND ST_Within(:point, g.boundary)", nativeQuery = true)
    List<Geofence> findServiceZonesForPoint(@Param("point") Point point);
}
