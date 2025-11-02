package com.journal.notification.repository;

import com.journal.notification.model.Notification;
import com.journal.user.model.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadIsFalseOrderByCreatedAtDesc(UserAccount user);
}
