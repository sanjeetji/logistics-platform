package com.logistics.auth.repository;

import com.logistics.auth.model.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserTenantRepository extends JpaRepository<UserTenant, UUID> {
    List<UserTenant> findByUserId(UUID userId);

    List<UserTenant> findByOrganizationId(Long organizationId);
}
