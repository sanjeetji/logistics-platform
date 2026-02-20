package com.logistics.fleet.repository;

import com.logistics.fleet.model.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverGeospatialRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT * FROM drivers d WHERE ST_DWithin(d.current_location, :location, :radius)", nativeQuery = true)
    List<Driver> findDriversWithinRadius(@Param("location") Point location, @Param("radius") double radiusInMeters);

    @Query(value = "SELECT * FROM drivers d WHERE ST_Within(d.current_location, ST_GeomFromText(:wktPolygon, 4326))", nativeQuery = true)
    List<Driver> findDriversInZone(@Param("wktPolygon") String wktPolygon);

    @Query(value = "SELECT * FROM drivers d WHERE d.status = 'AVAILABLE' AND ST_DWithin(d.current_location, :location, :radius) ORDER BY ST_Distance(d.current_location, :location) ASC", nativeQuery = true)
    List<Driver> findNearestAvailableDrivers(@Param("location") Point location, @Param("radius") double radiusInMeters);
}
