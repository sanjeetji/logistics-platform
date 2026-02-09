package com.logistics.masterdata.repository;

import com.logistics.masterdata.model.ServiceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceZoneRepository extends JpaRepository<ServiceZone, Long> {
    List<ServiceZone> findByCityId(Long cityId);
}
