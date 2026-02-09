package com.logistics.notification.repository;

import com.logistics.notification.model.Notification;
import com.logistics.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(NotificationStatus status);
}
