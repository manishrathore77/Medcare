package com.medcare.api.model;


/**
 * Data transfer object for AuditLog in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {

    private Long id;
    private Long userId;
    private String action;
    private String entityName;
    private Long entityId;
    private String oldValue;
    private String newValue;
}
