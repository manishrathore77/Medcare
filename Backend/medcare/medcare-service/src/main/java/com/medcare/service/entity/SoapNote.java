package com.medcare.service.entity;


/**
 * JPA entity representing a soapnote in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "soap_notes")
@Getter
@Setter
public class SoapNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Appointment appointment;

    @Lob
    private String subjective;

    @Lob
    private String objective;

    @Lob
    private String assessment;

    @Lob
    private String plan;
}
