package com.medcare.api.model;


/**
 * Data transfer object for Notification in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationStatus status;
    private Long relatedId;
}
