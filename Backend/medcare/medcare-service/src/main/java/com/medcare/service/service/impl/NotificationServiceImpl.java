package com.medcare.service.service.impl;

import com.medcare.service.entity.Notification;
import com.medcare.service.repository.NotificationRepository;
import com.medcare.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link NotificationService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAll() {
        log.debug("Notification listAll start");
        List<Notification> all = notificationRepository.findAll();
        log.debug("Notification listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> getById(Long id) {
        log.debug("Notification lookup id={}", id);
        return notificationRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created id={} userId={} title={}",
                saved.getId(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getTitle());
        return saved;
    }
}
