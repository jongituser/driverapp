package org.driver.driverapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverAnalyticsDTO {
    private long totalDeliveries;
    private long overdueDeliveries;
    private double averageEtaMinutes;
    private double averageDeliveryDurationMinutes;
}
