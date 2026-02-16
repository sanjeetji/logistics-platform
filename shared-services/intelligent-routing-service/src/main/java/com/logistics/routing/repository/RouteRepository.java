package com.logistics.routing.repository;

import com.logistics.routing.model.Route;
import com.logistics.routing.model.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByRouteId(String routeId);
    
    List<Route> findByVehicleId(Long vehicleId);
    
    List<Route> findByDriverId(Long driverId);
    
    List<Route> findByRouteDate(LocalDate routeDate);
    
    List<Route> findByStatus(RouteStatus status);
    
    @Query("SELECT r FROM Route r WHERE r.vehicleId = :vehicleId AND r.routeDate = :date")
    Optional<Route> findByVehicleAndDate(Long vehicleId, LocalDate date);
    
    @Query("SELECT r FROM Route r WHERE r.driverId = :driverId AND r.status = :status")
    List<Route> findByDriverIdAndStatus(Long driverId, RouteStatus status);
}
