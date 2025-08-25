package org.driver.driverapp.dto.analytics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.EthiopianRegion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeospatialAnalyticsDTO {
    
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<RegionDeliveryData> regionData;
    private List<WoredaDeliveryData> woredaData;
    private BigDecimal totalDeliveries;
    private BigDecimal totalRevenue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionDeliveryData {
        private EthiopianRegion region;
        private Long deliveryCount;
        private BigDecimal revenue;
        private Long activeDrivers;
        private Long activePartners;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WoredaDeliveryData {
        private String woreda;
        private EthiopianRegion region;
        private Long deliveryCount;
        private BigDecimal revenue;
        private Long activeDrivers;
        private Long activePartners;
    }
}

