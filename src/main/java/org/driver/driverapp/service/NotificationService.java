package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.notification.request.SendNotificationRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationStatus;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.NotificationMapper;
import org.driver.driverapp.model.Notification;
import org.driver.driverapp.model.NotificationTemplate;
import org.driver.driverapp.repository.NotificationRepository;
import org.driver.driverapp.service.notification.EmailService;
import org.driver.driverapp.service.notification.PushService;
import org.driver.driverapp.service.notification.SmsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationMapper notificationMapper;
    private final SmsService smsService;
    private final EmailService emailService;
    private final PushService pushService;

    @Transactional
    public NotificationResponseDTO sendNotification(SendNotificationRequestDTO requestDTO) {
        log.info("Sending notification to recipient: {} with template: {}", requestDTO.getRecipientId(), requestDTO.getTemplateCode());

        // Get template with fallback to English
        NotificationTemplate template = notificationTemplateService.getTemplateEntityByCodeAndLanguage(
                requestDTO.getTemplateCode(), requestDTO.getLanguage());

        // Create notification record
        Notification notification = Notification.builder()
                .recipientId(requestDTO.getRecipientId())
                .type(template.getType())
                .language(template.getLanguage())
                .templateId(template.getId())
                .status(NotificationStatus.PENDING)
                .active(true)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            // Resolve template and replace placeholders
            String resolvedMessage = resolveTemplate(template.getBody(), requestDTO.getPlaceholders());
            String resolvedSubject = template.getSubject() != null ? 
                    resolveTemplate(template.getSubject(), requestDTO.getPlaceholders()) : null;

            // Send notification based on type
            boolean sent = sendNotificationByType(template.getType(), requestDTO.getRecipientId(), 
                    resolvedSubject, resolvedMessage, requestDTO.getPlaceholders());

            // Update notification status
            if (sent) {
                savedNotification.markAsSent();
            } else {
                savedNotification.markAsFailed("Failed to send notification");
            }

            notificationRepository.save(savedNotification);

            log.info("Notification sent successfully: {}", savedNotification.getId());
            return notificationMapper.toResponseDTO(savedNotification);

        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            savedNotification.markAsFailed(e.getMessage());
            notificationRepository.save(savedNotification);
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    private boolean sendNotificationByType(NotificationType type, Long recipientId, String subject, 
                                         String message, Map<String, String> placeholders) {
        switch (type) {
            case SMS:
                return sendSmsNotification(recipientId, message);
            case EMAIL:
                return sendEmailNotification(recipientId, subject, message);
            case PUSH:
                return sendPushNotification(recipientId, subject, message, placeholders);
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + type);
        }
    }

    private boolean sendSmsNotification(Long recipientId, String message) {
        // In a real implementation, you would get the phone number from user service
        String phoneNumber = getPhoneNumberForUser(recipientId);
        return smsService.sendSms(phoneNumber, message);
    }

    private boolean sendEmailNotification(Long recipientId, String subject, String message) {
        // In a real implementation, you would get the email from user service
        String email = getEmailForUser(recipientId);
        return emailService.sendEmail(email, subject, message);
    }

    private boolean sendPushNotification(Long recipientId, String title, String message, Map<String, String> placeholders) {
        // In a real implementation, you would get the device token from user service
        String deviceToken = getDeviceTokenForUser(recipientId);
        return pushService.sendPushNotificationWithData(deviceToken, title, message, placeholders);
    }

    private String resolveTemplate(String template, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return template;
        }

        String resolved = template;
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String value = placeholders.get(placeholder);
            if (value != null) {
                resolved = resolved.replace("{" + placeholder + "}", value);
            } else {
                log.warn("Placeholder not found: {}", placeholder);
            }
        }

        return resolved;
    }

    // Stub methods for getting user information
    private String getPhoneNumberForUser(Long userId) {
        // In a real implementation, this would call a user service
        return "+251912345678"; // Stub phone number
    }

    private String getEmailForUser(Long userId) {
        // In a real implementation, this would call a user service
        return "user" + userId + "@example.com"; // Stub email
    }

    private String getDeviceTokenForUser(Long userId) {
        // In a real implementation, this would call a user service
        return "device_token_" + userId; // Stub device token
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long id) {
        log.info("Getting notification by id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        return notificationMapper.toResponseDTO(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByRecipient(Long recipientId) {
        log.info("Getting notifications for recipient: {}", recipientId);

        List<Notification> notifications = notificationRepository.findByRecipientIdAndActiveTrue(recipientId);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getNotificationsByRecipient(Long recipientId, Pageable pageable) {
        log.info("Getting notifications for recipient: {} with pagination", recipientId);

        Page<Notification> notifications = notificationRepository.findByRecipientIdAndActiveTrue(recipientId, pageable);
        return notifications.map(notificationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByStatus(NotificationStatus status) {
        log.info("Getting notifications by status: {}", status);

        List<Notification> notifications = notificationRepository.findByStatusAndActiveTrue(status);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByType(NotificationType type) {
        log.info("Getting notifications by type: {}", type);

        List<Notification> notifications = notificationRepository.findByTypeAndActiveTrue(type);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByLanguage(NotificationLanguage language) {
        log.info("Getting notifications by language: {}", language);

        List<Notification> notifications = notificationRepository.findByLanguageAndActiveTrue(language);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getPendingNotifications() {
        log.info("Getting pending notifications");

        List<Notification> notifications = notificationRepository.findByStatusAndActiveTrueOrderByCreatedAtAsc(NotificationStatus.PENDING);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getFailedNotifications() {
        log.info("Getting failed notifications");

        List<Notification> notifications = notificationRepository.findByStatusAndActiveTrueOrderByCreatedAtDesc(NotificationStatus.FAILED);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByRecipient(Long recipientId) {
        return notificationRepository.countByRecipientIdAndActiveTrue(recipientId);
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByStatus(NotificationStatus status) {
        return notificationRepository.countByStatusAndActiveTrue(status);
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByType(NotificationType type) {
        return notificationRepository.countByTypeAndActiveTrue(type);
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByLanguage(NotificationLanguage language) {
        return notificationRepository.countByLanguageAndActiveTrue(language);
    }

    @Transactional(readOnly = true)
    public long getNotificationCountByRecipientAndStatus(Long recipientId, NotificationStatus status) {
        return notificationRepository.countByRecipientIdAndStatusAndActiveTrue(recipientId, status);
    }
}
