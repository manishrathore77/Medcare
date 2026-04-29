package com.medcare.service.entity;


/**
 * JPA entity representing a inventorylog in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_logs")
@Getter
@Setter
public class InventoryLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Medicine medicine;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    private Integer quantity;
    private String reason;

    public enum ChangeType {
        IN, OUT
    }
}
