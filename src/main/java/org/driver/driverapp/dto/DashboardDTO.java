package org.driver.driverapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDTO {
    private long totalDeliveries;
    private long inProgressDeliveries;
    private long deliveredToday;
    private long overdueDeliveries;
    private double averageEtaMinutes;
    private double averageDeliveryDurationMinutes;

    private List<DriverSummaryDTO> topDrivers;
    private List<LowInventoryAlertDTO> lowInventoryAlerts;
}
