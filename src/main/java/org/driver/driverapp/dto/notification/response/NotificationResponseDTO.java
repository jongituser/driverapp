package org.driver.driverapp.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationStatus;
import org.driver.driverapp.enums.NotificationType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private Long recipientId;
    private NotificationType type;
    private NotificationLanguage language;
    private Long templateId;
    private NotificationStatus status;
    private Instant sentAt;
    private String errorMessage;
    private boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}
