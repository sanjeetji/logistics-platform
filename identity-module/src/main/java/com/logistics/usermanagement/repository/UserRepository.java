package com.logistics.usermanagement.repository;

import com.logistics.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByTenantId(String tenantId);
    
    List<User> findByTenantIdAndStatus(String tenantId, User.UserStatus status);
    
    List<User> findByTenantIdAndUserType(String tenantId, User.UserType userType);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.tenantId = :tenantId")
    List<User> findByRoleIdAndTenantId(@Param("roleId") Long roleId, @Param("tenantId") String tenantId);
    
    boolean existsByEmail(String email);
    
    long countByTenantIdAndStatus(String tenantId, User.UserStatus status);
}
