package com.hilmatrix.montrack.repository;

import com.hilmatrix.montrack.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // You can define custom query methods here if needed
}
