package org.driver.driverapp.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationTemplateRequestDTO {

    @NotBlank(message = "Template code is required")
    private String code;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Language is required")
    private NotificationLanguage language;

    private String subject; // for email notifications

    @NotBlank(message = "Template body is required")
    private String body;
}
