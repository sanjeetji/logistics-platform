package com.logistics.routing.repository;

import com.logistics.routing.model.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {
    List<Waypoint> findByRouteId(Long routeId);
    List<Waypoint> findByOrderId(String orderId);
}
