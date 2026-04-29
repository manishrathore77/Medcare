package com.medcare.service.repository;


/**
 * Spring Data repository for {@link com.medcare.service.entity.Notification} persistence.
 */

import com.medcare.service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
