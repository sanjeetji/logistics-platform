package com.logistics.integration.repository;

import com.logistics.integration.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    List<WebhookEvent> findByStatus(WebhookEvent.WebhookStatus status);

    List<WebhookEvent> findByTenantId(String tenantId);
}
