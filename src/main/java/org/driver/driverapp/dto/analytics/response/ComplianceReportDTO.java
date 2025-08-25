package org.driver.driverapp.dto.analytics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceReportDTO {
    
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<DriverComplianceData> driverCompliance;
    private List<PartnerComplianceData> partnerCompliance;
    private ComplianceSummary summary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverComplianceData {
        private Long driverId;
        private String driverName;
        private String phoneNumber;
        private Long totalDeliveries;
        private Long missedDeliveries;
        private Long lateDeliveries;
        private Long onTimeDeliveries;
        private BigDecimal complianceScore;
        private String status; // EXCELLENT, GOOD, FAIR, POOR
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartnerComplianceData {
        private Long partnerId;
        private String partnerName;
        private String businessName;
        private Long totalOrders;
        private Long expiredInventoryItems;
        private Long lowStockItems;
        private Boolean kycCompleted;
        private BigDecimal complianceScore;
        private String status; // EXCELLENT, GOOD, FAIR, POOR
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplianceSummary {
        private Long totalDrivers;
        private Long compliantDrivers;
        private BigDecimal averageDriverScore;
        private Long totalPartners;
        private Long compliantPartners;
        private BigDecimal averagePartnerScore;
    }
}

