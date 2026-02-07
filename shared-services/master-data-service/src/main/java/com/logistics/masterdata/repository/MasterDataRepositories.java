package com.logistics.masterdata.repository;

import com.logistics.masterdata.model.City;
import com.logistics.masterdata.model.ServiceZone;
import com.logistics.masterdata.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByCountry(String country);
    Optional<City> findByNameAndCountry(String name, String country);
}

@Repository
interface ServiceZoneRepository extends JpaRepository<ServiceZone, Long> {
    List<ServiceZone> findByCityId(Long cityId);
}

@Repository
interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    Optional<VehicleType> findByName(String name);
    List<VehicleType> findByIsActiveTrue();
}
