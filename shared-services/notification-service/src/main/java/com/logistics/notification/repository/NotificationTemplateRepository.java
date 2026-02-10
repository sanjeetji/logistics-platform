package com.logistics.notification.repository;

import com.logistics.notification.model.NotificationTemplate;
import com.logistics.notification.model.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByTemplateId(String templateId);

    Optional<NotificationTemplate> findByTemplateName(String templateName);

    List<NotificationTemplate> findByChannel(NotificationChannel channel);

    List<NotificationTemplate> findByActive(Boolean active);
}
