package com.journal.notification.controller;

import com.journal.notification.dto.NotificationResponse;
import com.journal.notification.service.NotificationService;
import com.journal.user.model.UserAccount;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread")
    public List<NotificationResponse> unread(@AuthenticationPrincipal UserAccount user) {
        return notificationService.unread(user);
    }
}
