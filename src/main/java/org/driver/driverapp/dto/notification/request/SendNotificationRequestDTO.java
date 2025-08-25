package org.driver.driverapp.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.NotificationLanguage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequestDTO {

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Template code is required")
    private String templateCode;

    @NotNull(message = "Language is required")
    private NotificationLanguage language;

    private Map<String, String> placeholders; // e.g., {"name": "John", "deliveryId": "123"}
}
