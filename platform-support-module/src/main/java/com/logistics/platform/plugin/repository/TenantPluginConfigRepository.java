package com.logistics.platform.plugin.repository;

import com.logistics.platform.plugin.model.TenantPluginConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantPluginConfigRepository extends JpaRepository<TenantPluginConfig, Long> {

    List<TenantPluginConfig> findByTenantIdAndIsActiveTrue(String tenantId);

    Optional<TenantPluginConfig> findByTenantIdAndPluginId(String tenantId, String pluginId);
}
