package com.logistics.masterdata.service;

import com.logistics.masterdata.model.City;
import com.logistics.masterdata.model.ServiceZone;
import com.logistics.masterdata.model.VehicleType;
import com.logistics.masterdata.repository.CityRepository;
import com.logistics.masterdata.repository.ServiceZoneRepository;
import com.logistics.masterdata.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDataService {

    private final CityRepository cityRepository;
    private final ServiceZoneRepository zoneRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Cacheable(value = "cities", key = "'all'")
    public List<City> getAllCities() {
        log.info("Fetching all cities from database");
        return cityRepository.findAll();
    }

    @Cacheable(value = "cities", key = "#country")
    public List<City> getCitiesByCountry(String country) {
        log.info("Fetching cities for country: {}", country);
        return cityRepository.findByCountry(country);
    }

    @Transactional
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    @Cacheable(value = "zones", key = "#cityId")
    public List<ServiceZone> getZonesByCity(Long cityId) {
        log.info("Fetching zones for city: {}", cityId);
        return zoneRepository.findByCityId(cityId);
    }

    @Transactional
    public ServiceZone createZone(ServiceZone zone) {
        return zoneRepository.save(zone);
    }

    @Cacheable(value = "vehicleTypes", key = "'all'")
    public List<VehicleType> getAllVehicleTypes() {
        log.info("Fetching all vehicle types from database");
        return vehicleTypeRepository.findAll();
    }

    @Cacheable(value = "vehicleTypes", key = "'active'")
    public List<VehicleType> getActiveVehicleTypes() {
        log.info("Fetching active vehicle types from database");
        return vehicleTypeRepository.findByIsActiveTrue();
    }

    @Transactional
    public VehicleType createVehicleType(VehicleType vehicleType) {
        return vehicleTypeRepository.save(vehicleType);
    }
}
