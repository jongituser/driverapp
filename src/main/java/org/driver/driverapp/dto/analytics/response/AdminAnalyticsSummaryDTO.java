package org.driver.driverapp.dto.analytics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsSummaryDTO {
    
    // Delivery metrics
    private Long totalDeliveriesToday;
    private Long totalDeliveriesThisWeek;
    private Long totalDeliveriesThisMonth;
    private Long failedDeliveriesToday;
    private Long failedDeliveriesThisWeek;
    private Long failedDeliveriesThisMonth;
    
    // Driver metrics
    private Long activeDriversToday;
    private Long totalDrivers;
    private Long driversOnDelivery;
    private Long driversAvailable;
    
    // Revenue metrics
    private BigDecimal totalRevenueToday;
    private BigDecimal totalRevenueThisWeek;
    private BigDecimal totalRevenueThisMonth;
    private BigDecimal averageOrderValue;
    
    // Partner metrics
    private Long activePartners;
    private Long totalPartners;
    
    // Customer metrics
    private Long newCustomersToday;
    private Long totalCustomers;
    
    // Date range
    private LocalDate fromDate;
    private LocalDate toDate;
}

