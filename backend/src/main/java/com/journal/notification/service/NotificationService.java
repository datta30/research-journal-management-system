package com.journal.notification.service;

import com.journal.notification.dto.NotificationResponse;
import com.journal.notification.model.Notification;
import com.journal.notification.model.NotificationType;
import com.journal.notification.repository.NotificationRepository;
import com.journal.user.model.UserAccount;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification notify(UserAccount user, NotificationType type, String message, String link) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .resourceLink(link)
                .build();
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> unread(UserAccount user) {
        return notificationRepository.findByUserAndReadIsFalseOrderByCreatedAtDesc(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getResourceLink(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
