package com.logistics.driver.repository;

import com.logistics.driver.model.DriverProfile;
import com.logistics.driver.model.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    
    Optional<DriverProfile> findByDriverId(Long driverId);
    
    List<DriverProfile> findByOnboardingStatus(OnboardingStatus status);
    
    boolean existsByDriverId(Long driverId);
}
