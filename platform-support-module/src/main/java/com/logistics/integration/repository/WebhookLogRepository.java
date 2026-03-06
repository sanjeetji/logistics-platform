package com.logistics.integration.repository;

import com.logistics.integration.model.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("integrationWebhookLogRepository")
public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
}
