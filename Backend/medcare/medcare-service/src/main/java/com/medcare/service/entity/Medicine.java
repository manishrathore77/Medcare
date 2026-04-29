package com.medcare.service.entity;


/**
 * JPA entity representing a medicine in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "medicines")
@Getter
@Setter
public class Medicine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String batchNo;
    private LocalDate expiryDate;

    private Integer stockQuantity;
    private Integer reorderLevel = 10;

    private Double unitPrice;
}
