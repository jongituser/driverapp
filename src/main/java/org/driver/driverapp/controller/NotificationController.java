package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.notification.request.SendNotificationRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationStatus;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<NotificationResponseDTO> sendNotification(@Valid @RequestBody SendNotificationRequestDTO requestDTO) {
        log.info("Sending notification to recipient: {}", requestDTO.getRecipientId());
        NotificationResponseDTO response = notificationService.sendNotification(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        log.info("Getting notification by id: {}", id);
        NotificationResponseDTO response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recipient/{recipientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipient(@PathVariable Long recipientId) {
        log.info("Getting notifications for recipient: {}", recipientId);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByRecipient(recipientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recipient/{recipientId}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotificationsByRecipient(@PathVariable Long recipientId, Pageable pageable) {
        log.info("Getting notifications for recipient: {} with pagination", recipientId);
        Page<NotificationResponseDTO> response = notificationService.getNotificationsByRecipient(recipientId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        log.info("Getting notifications by status: {}", status);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByType(@PathVariable NotificationType type) {
        log.info("Getting notifications by type: {}", type);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/language/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByLanguage(@PathVariable NotificationLanguage language) {
        log.info("Getting notifications by language: {}", language);
        List<NotificationResponseDTO> response = notificationService.getNotificationsByLanguage(language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getPendingNotifications() {
        log.info("Getting pending notifications");
        List<NotificationResponseDTO> response = notificationService.getPendingNotifications();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponseDTO>> getFailedNotifications() {
        log.info("Getting failed notifications");
        List<NotificationResponseDTO> response = notificationService.getFailedNotifications();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/recipient/{recipientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Long> getNotificationCountByRecipient(@PathVariable Long recipientId) {
        log.info("Getting notification count for recipient: {}", recipientId);
        long count = notificationService.getNotificationCountByRecipient(recipientId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getNotificationCountByStatus(@PathVariable NotificationStatus status) {
        log.info("Getting notification count by status: {}", status);
        long count = notificationService.getNotificationCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getNotificationCountByType(@PathVariable NotificationType type) {
        log.info("Getting notification count by type: {}", type);
        long count = notificationService.getNotificationCountByType(type);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/language/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getNotificationCountByLanguage(@PathVariable NotificationLanguage language) {
        log.info("Getting notification count by language: {}", language);
        long count = notificationService.getNotificationCountByLanguage(language);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/recipient/{recipientId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Long> getNotificationCountByRecipientAndStatus(@PathVariable Long recipientId, 
                                                                       @PathVariable NotificationStatus status) {
        log.info("Getting notification count for recipient: {} and status: {}", recipientId, status);
        long count = notificationService.getNotificationCountByRecipientAndStatus(recipientId, status);
        return ResponseEntity.ok(count);
    }
}
