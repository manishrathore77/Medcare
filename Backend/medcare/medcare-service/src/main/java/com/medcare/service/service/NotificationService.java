package com.medcare.service.service;

import com.medcare.service.entity.Notification;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for user notifications.
 */
public interface NotificationService {

    /**
     * @return all notifications
     */
    List<Notification> getAll();

    /**
     * @param id notification id
     * @return optional notification when found
     */
    Optional<Notification> getById(Long id);

    /**
     * Persists a new notification.
     *
     * @param notification populated entity
     * @return saved notification
     */
    Notification createNotification(Notification notification);
}
