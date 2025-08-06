package org.driver.driverapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverSummaryDTO {
    private String name;
    private long totalDeliveries;
    private double onTimePercentage;
    private double averageDeliveryTime;
}
