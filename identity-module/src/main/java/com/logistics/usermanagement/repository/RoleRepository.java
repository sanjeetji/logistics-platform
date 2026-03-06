package com.logistics.usermanagement.repository;

import com.logistics.usermanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByNameAndTenantId(String name, String tenantId);
    
    List<Role> findByTenantId(String tenantId);
    
    List<Role> findByTenantIdAndActive(String tenantId, Boolean active);
    
    List<Role> findByRoleType(Role.RoleType roleType);
    
    boolean existsByNameAndTenantId(String name, String tenantId);
}
