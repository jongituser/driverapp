package org.driver.driverapp.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateResponseDTO {

    private Long id;
    private String code;
    private NotificationType type;
    private NotificationLanguage language;
    private String subject;
    private String body;
    private boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}
