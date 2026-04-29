package com.medcare.service.controller;


/**
 * REST implementation of {@link com.medcare.api.controller.NotificationController}.
 */

import com.medcare.api.controller.NotificationController;
import com.medcare.api.model.NotificationDto;
import com.medcare.service.entity.Notification;
import com.medcare.service.entity.User;
import com.medcare.service.generic.dto.ApiResponse;
import com.medcare.service.generic.dto.PagedResponse;
import com.medcare.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_NOTIFICATIONS_READ')")
    public ResponseEntity<PagedResponse<NotificationDto>> list(int page, int size) {
        List<Notification> all = notificationService.getAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<NotificationDto> content = all.subList(from, to).stream().map(this::toNotificationDto).collect(Collectors.toList());
        PagedResponse<NotificationDto> resp = new PagedResponse<>(content, all.size(), page, size,
                (all.size() + size - 1) / size, to == all.size());
        return ResponseEntity.ok(resp);
    }

    @Override
    @PreAuthorize("hasAuthority('MEDCARE_NOTIFICATIONS_WRITE')")
    public ResponseEntity<ApiResponse<NotificationDto>> create(NotificationDto dto) {
        Notification entity = toNotification(dto);
        Notification saved = notificationService.createNotification(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Created", toNotificationDto(saved), HttpStatus.CREATED.value()));
    }

    private NotificationDto toNotificationDto(Notification e) {
        NotificationDto d = new NotificationDto();
        d.setId(e.getId());
        d.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        d.setTitle(e.getTitle());
        d.setMessage(e.getMessage());
        d.setChannel(e.getChannel() != null ? com.medcare.api.model.NotificationChannel.valueOf(e.getChannel().name()) : null);
        d.setStatus(e.getStatus() != null ? com.medcare.api.model.NotificationStatus.valueOf(e.getStatus().name()) : null);
        d.setRelatedId(e.getRelatedId());
        return d;
    }

    private Notification toNotification(NotificationDto d) {
        Notification e = new Notification();
        e.setTitle(d.getTitle());
        e.setMessage(d.getMessage());
        e.setChannel(d.getChannel() != null ? Notification.Channel.valueOf(d.getChannel().name()) : null);
        e.setStatus(d.getStatus() != null ? Notification.Status.valueOf(d.getStatus().name()) : null);
        e.setRelatedId(d.getRelatedId());
        if (d.getUserId() != null) {
            User u = new User();
            u.setId(d.getUserId());
            e.setUser(u);
        }
        return e;
    }
}
