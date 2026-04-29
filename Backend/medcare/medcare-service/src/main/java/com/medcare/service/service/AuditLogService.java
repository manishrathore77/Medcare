package com.medcare.service.service;

import com.medcare.service.entity.AuditLog;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for immutable audit event rows.
 */
public interface AuditLogService {

    /**
     * @return all audit logs
     */
    List<AuditLog> getAll();

    /**
     * @param id audit log id
     * @return optional log when found
     */
    Optional<AuditLog> getById(Long id);

    /**
     * Permanently removes an audit row (privileged operation).
     *
     * @param id audit log id
     */
    void delete(Long id);
}
