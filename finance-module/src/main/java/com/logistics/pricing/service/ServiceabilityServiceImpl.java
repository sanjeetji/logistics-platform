package com.logistics.pricing.service;

import com.logistics.pricing.model.ServiceableZone;
import com.logistics.pricing.repository.ServiceableZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceabilityServiceImpl implements ServiceabilityService {

    private final ServiceableZoneRepository serviceableZoneRepository;
    private final DistanceService distanceService;

    @Override
    public boolean isServiceable(double latitude, double longitude) {
        return findServiceableZone(latitude, longitude).isPresent();
    }

    @Override
    public java.util.Optional<ServiceableZone> findServiceableZone(double latitude, double longitude) {
        // Cache this if possible, for now fetch all active zones
        List<ServiceableZone> activeZones = serviceableZoneRepository.findByActiveTrue();

        for (ServiceableZone zone : activeZones) {
            double distance = distanceService.calculateDistance(
                    latitude, longitude,
                    zone.getLatitude(), zone.getLongitude());

            if (distance <= zone.getRadiusKm()) {
                log.debug("Location ({}, {}) is inside zone: {}", latitude, longitude, zone.getZoneName());
                return java.util.Optional.of(zone);
            }
        }

        log.debug("Location ({}, {}) is NOT serviceable", latitude, longitude);
        return java.util.Optional.empty();
    }

    @Override
    public boolean isRouteServiceable(double pickupLat, double pickupLon, double dropLat, double dropLon) {
        // Both pickup and drop must be serviceable
        return isServiceable(pickupLat, pickupLon) && isServiceable(dropLat, dropLon);
    }
}
