package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.exception.ResourceNotFoundException;
import com.hilmatrix.montrack.model.Notification;
import com.hilmatrix.montrack.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private NotificationRepository notificationRepository;
    private final JwtDecoder jwtDecoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository, JwtDecoder jwtDecoder, JdbcTemplate jdbcTemplate) {
        this.notificationRepository = notificationRepository;
        this.jwtDecoder = jwtDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getEmailFromJwt(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public Long getUserIdFromJwt(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Long.class);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestHeader("Authorization") String authorizationHeader) {
        String email = getEmailFromJwt(authorizationHeader);
        Long userId = getUserIdFromJwt(email);
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/notification/{id}")
    public ResponseEntity<Notification> getNotificationById(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        String email = getEmailFromJwt(authorizationHeader);
        Long userId = getUserIdFromJwt(email);

        Optional<Notification> notification = notificationRepository.findByIdAndUserId(id, userId);
        return notification.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/notifications")
    public ResponseEntity<Notification> createNotification(
            @RequestHeader("Authorization") String authorizationHeader, @RequestBody Notification notification) {
        String email = getEmailFromJwt(authorizationHeader);
        Long userId = getUserIdFromJwt(email);

        notification.setUserId(userId); // Set the userId for the notification
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        Notification createdNotification = notificationRepository.save(notification);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @PutMapping("/notification/{id}")
    public ResponseEntity<Notification> updateNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id, @RequestBody Notification notificationDetails) {
        String email = getEmailFromJwt(authorizationHeader);
        Long userId = getUserIdFromJwt(email);

        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for this id :: " + id));

        notification.setName(notificationDetails.getName());
        notification.setDescription(notificationDetails.getDescription());
        notification.setIsUnread(notificationDetails.getIsUnread());
        notification.setUpdatedAt(LocalDateTime.now());

        final Notification updatedNotification = notificationRepository.save(notification);
        return ResponseEntity.ok(updatedNotification);
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<Void> deleteNotification(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        String email = getEmailFromJwt(authorizationHeader);
        Long userId = getUserIdFromJwt(email);

        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for this id :: " + id));

        notificationRepository.delete(notification);
        return ResponseEntity.noContent().build();
    }
}
