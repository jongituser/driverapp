package org.driver.driverapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAnalyticsDTO {
    private long totalDeliveries;
    private long overdueDeliveries;
    private double averageEtaMinutes;
    private double averageDeliveryDurationMinutes;

    private Map<Long, DriverAnalyticsDTO> driverStats;
    private Map<Long, PartnerAnalyticsDTO> partnerStats;

}

