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
public class PartnerAnalyticsSummaryDTO {
    
    // Order metrics
    private Long totalOrdersToday;
    private Long totalOrdersThisWeek;
    private Long totalOrdersThisMonth;
    private Long completedOrdersToday;
    private Long completedOrdersThisWeek;
    private Long completedOrdersThisMonth;
    
    // Billing metrics
    private BigDecimal totalBillingToday;
    private BigDecimal totalBillingThisWeek;
    private BigDecimal totalBillingThisMonth;
    private BigDecimal pendingBillingAmount;
    private BigDecimal paidBillingAmount;
    
    // Inventory metrics
    private Long totalInventoryItems;
    private Long lowStockItems;
    private Long outOfStockItems;
    private Long expiredItems;
    private BigDecimal totalInventoryValue;
    
    // Performance metrics
    private BigDecimal averageOrderValue;
    private Long totalCustomers;
    private Long repeatCustomers;
    
    // Date range
    private LocalDate fromDate;
    private LocalDate toDate;
}

