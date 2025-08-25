package org.driver.driverapp.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushDeliveryReport {
    
    private boolean success;
    private int totalDevices;
    private int successfulDeliveries;
    private int failedDeliveries;
    private List<String> failedDeviceTokens;
    private String errorMessage;
    private long timestamp;
}
