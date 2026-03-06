package com.logistics.team.repository;

import com.logistics.team.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HubRepository extends JpaRepository<Hub, String> {
    List<Hub> findByRegionId(String regionId);
}
