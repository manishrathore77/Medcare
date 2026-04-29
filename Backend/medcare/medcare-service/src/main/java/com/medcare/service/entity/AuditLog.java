package com.medcare.service.entity;


/**
 * JPA entity representing a auditlog in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String action;
    private String entityName;
    private Long entityId;

    @Lob
    private String oldValue;

    @Lob
    private String newValue;
}
