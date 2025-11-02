package com.journal.notification.dto;

import com.journal.notification.model.NotificationType;
import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        String resourceLink,
        boolean read,
        OffsetDateTime createdAt
) {
}
