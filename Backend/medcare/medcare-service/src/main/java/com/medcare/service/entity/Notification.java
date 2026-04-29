package com.medcare.service.entity;


/**
 * JPA entity representing a notification in the Medcare domain model.
 */

import com.medcare.service.generic.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long relatedId;

    public enum Channel {
        SMS, EMAIL, PUSH, IN_APP
    }

    public enum Status {
        PENDING, SENT, FAILED, READ
    }
}
