package org.driver.driverapp.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDeliveryReport {
    
    private boolean success;
    private String messageId;
    private String status;
    private String errorMessage;
    private long timestamp;
}
