package com.medcare.service.service.impl;

import com.medcare.service.entity.AuditLog;
import com.medcare.service.repository.AuditLogRepository;
import com.medcare.service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Default {@link AuditLogService} implementation using JPA repositories.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAll() {
        log.debug("AuditLog listAll start");
        List<AuditLog> all = auditLogRepository.findAll();
        log.debug("AuditLog listAll count={}", all.size());
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AuditLog> getById(Long id) {
        log.debug("AuditLog lookup id={}", id);
        return auditLogRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        log.warn("AuditLog delete id={}", id);
        auditLogRepository.deleteById(id);
    }
}
