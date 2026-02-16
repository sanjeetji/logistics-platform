package com.logistics.pricing.repository;

import com.logistics.pricing.model.ServiceableZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceableZoneRepository extends JpaRepository<ServiceableZone, Long> {

    List<ServiceableZone> findByActiveTrue();

    @Query("SELECT sz FROM ServiceableZone sz WHERE sz.active = true")
    List<ServiceableZone> findAllActiveZones();
}
