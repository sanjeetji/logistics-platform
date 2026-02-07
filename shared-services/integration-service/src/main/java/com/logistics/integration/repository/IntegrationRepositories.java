package com.logistics.integration.repository;

import com.logistics.integration.model.EcommercePlatform;
import com.logistics.integration.model.WebhookConfig;
import com.logistics.integration.model.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebhookConfigRepository extends JpaRepository<WebhookConfig, Long> {
    Optional<WebhookConfig> findByTenantIdAndPlatform(String tenantId, EcommercePlatform platform);
}

@Repository
interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
}
