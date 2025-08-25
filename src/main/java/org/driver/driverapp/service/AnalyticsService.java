package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.analytics.request.CreateAnalyticsRecordRequestDTO;
import org.driver.driverapp.dto.analytics.response.*;
import org.driver.driverapp.enums.AnalyticsRecordType;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.mapper.AnalyticsRecordMapper;
import org.driver.driverapp.model.AnalyticsRecord;
import org.driver.driverapp.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnalyticsService {
    
    private final AnalyticsRecordRepository analyticsRecordRepository;
    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AnalyticsRecordMapper analyticsRecordMapper;
    
    // Analytics Record Management
    public AnalyticsRecordResponseDTO createAnalyticsRecord(CreateAnalyticsRecordRequestDTO request) {
        log.info("Creating analytics record: type={}, entityId={}", request.getType(), request.getEntityId());
        
        AnalyticsRecord record = analyticsRecordMapper.toEntity(request);
        AnalyticsRecord savedRecord = analyticsRecordRepository.save(record);
        
        return analyticsRecordMapper.toResponseDTO(savedRecord);
    }
    
    public Page<AnalyticsRecordResponseDTO> getAnalyticsRecords(AnalyticsRecordType type, Pageable pageable) {
        Page<AnalyticsRecord> records = analyticsRecordRepository.findByTypeAndActiveTrue(type, pageable);
        return records.map(analyticsRecordMapper::toResponseDTO);
    }
    
    public AnalyticsRecordResponseDTO getAnalyticsRecord(Long id) {
        AnalyticsRecord record = analyticsRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics record not found"));
        return analyticsRecordMapper.toResponseDTO(record);
    }
    
    // Admin Dashboard Analytics
    public AdminAnalyticsSummaryDTO getAdminAnalyticsSummary(LocalDate fromDate, LocalDate toDate) {
        log.info("Generating admin analytics summary from {} to {}", fromDate, toDate);
        
        // Note: startDate and endDate are calculated but not used in current implementation
        // They are kept for potential future use in date-range specific queries
        Instant endDate = toDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        // Delivery metrics
        Long totalDeliveriesToday = deliveryRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        Long totalDeliveriesThisWeek = deliveryRepository.countByCreatedAtBetween(
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long totalDeliveriesThisMonth = deliveryRepository.countByCreatedAtBetween(
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long failedDeliveriesToday = deliveryRepository.countByStatusAndCreatedAtBetween(
                DeliveryStatus.DELIVERY_FAILED,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        Long failedDeliveriesThisWeek = deliveryRepository.countByStatusAndCreatedAtBetween(
                DeliveryStatus.DELIVERY_FAILED,
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long failedDeliveriesThisMonth = deliveryRepository.countByStatusAndCreatedAtBetween(
                DeliveryStatus.DELIVERY_FAILED,
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        // Driver metrics
        Long activeDriversToday = driverRepository.countByStatusAndLastActiveAtBetween(
                DriverStatus.AVAILABLE,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long totalDrivers = driverRepository.count();
        Long driversOnDelivery = driverRepository.countByStatus(DriverStatus.BUSY);
        Long driversAvailable = driverRepository.countByStatus(DriverStatus.AVAILABLE);
        
        // Revenue metrics
        BigDecimal totalRevenueToday = paymentRepository.sumAmountByStatusAndCreatedAtBetween(
                "COMPLETED",
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        BigDecimal totalRevenueThisWeek = paymentRepository.sumAmountByStatusAndCreatedAtBetween(
                "COMPLETED",
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        BigDecimal totalRevenueThisMonth = paymentRepository.sumAmountByStatusAndCreatedAtBetween(
                "COMPLETED",
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        BigDecimal averageOrderValue = paymentRepository.findAverageOrderValue();
        
        // Partner metrics
        Long activePartners = partnerRepository.countByActiveTrue();
        Long totalPartners = partnerRepository.count();
        
        // Customer metrics
        Long newCustomersToday = customerRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        Long totalCustomers = customerRepository.count();
        
        return AdminAnalyticsSummaryDTO.builder()
                .totalDeliveriesToday(totalDeliveriesToday)
                .totalDeliveriesThisWeek(totalDeliveriesThisWeek)
                .totalDeliveriesThisMonth(totalDeliveriesThisMonth)
                .failedDeliveriesToday(failedDeliveriesToday)
                .failedDeliveriesThisWeek(failedDeliveriesThisWeek)
                .failedDeliveriesThisMonth(failedDeliveriesThisMonth)
                .activeDriversToday(activeDriversToday)
                .totalDrivers(totalDrivers)
                .driversOnDelivery(driversOnDelivery)
                .driversAvailable(driversAvailable)
                .totalRevenueToday(totalRevenueToday != null ? totalRevenueToday : BigDecimal.ZERO)
                .totalRevenueThisWeek(totalRevenueThisWeek != null ? totalRevenueThisWeek : BigDecimal.ZERO)
                .totalRevenueThisMonth(totalRevenueThisMonth != null ? totalRevenueThisMonth : BigDecimal.ZERO)
                .averageOrderValue(averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO)
                .activePartners(activePartners)
                .totalPartners(totalPartners)
                .newCustomersToday(newCustomersToday)
                .totalCustomers(totalCustomers)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
    }
    
    // Partner Dashboard Analytics
    public PartnerAnalyticsSummaryDTO getPartnerAnalyticsSummary(Long partnerId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating partner analytics summary for partnerId={} from {} to {}", partnerId, fromDate, toDate);
        
        Instant startDate = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = toDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        // Order metrics
        Long totalOrdersToday = deliveryRepository.countByPartnerIdAndCreatedAtBetween(
                partnerId,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        Long totalOrdersThisWeek = deliveryRepository.countByPartnerIdAndCreatedAtBetween(
                partnerId,
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long totalOrdersThisMonth = deliveryRepository.countByPartnerIdAndCreatedAtBetween(
                partnerId,
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long completedOrdersToday = deliveryRepository.countByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                DeliveryStatus.DELIVERED,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        Long completedOrdersThisWeek = deliveryRepository.countByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                DeliveryStatus.DELIVERED,
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        Long completedOrdersThisMonth = deliveryRepository.countByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                DeliveryStatus.DELIVERED,
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        // Billing metrics
        BigDecimal totalBillingToday = paymentRepository.sumAmountByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                "COMPLETED",
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
        );
        
        BigDecimal totalBillingThisWeek = paymentRepository.sumAmountByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                "COMPLETED",
                LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        BigDecimal totalBillingThisMonth = paymentRepository.sumAmountByPartnerIdAndStatusAndCreatedAtBetween(
                partnerId,
                "COMPLETED",
                LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                Instant.now()
        );
        
        BigDecimal pendingBillingAmount = paymentRepository.sumAmountByPartnerIdAndStatus(partnerId, "PENDING");
        BigDecimal paidBillingAmount = paymentRepository.sumAmountByPartnerIdAndStatus(partnerId, "COMPLETED");
        
        // Inventory metrics
        Long totalInventoryItems = inventoryItemRepository.countByPartnerIdAndActiveTrue(partnerId);
        Long lowStockItems = inventoryItemRepository.countByPartnerIdAndQuantityLessThanAndActiveTrue(partnerId, 10L);
        Long outOfStockItems = inventoryItemRepository.countByPartnerIdAndQuantityEqualsAndActiveTrue(partnerId, 0L);
        Long expiredItems = inventoryItemRepository.countByPartnerIdAndExpiryDateBeforeAndActiveTrue(partnerId, LocalDate.now());
        BigDecimal totalInventoryValue = inventoryItemRepository.sumTotalValueByPartnerIdAndActiveTrue(partnerId);
        
        // Performance metrics
        BigDecimal averageOrderValue = paymentRepository.findAverageOrderValueByPartnerId(partnerId);
        Long totalCustomers = deliveryRepository.countDistinctCustomerIdByPartnerId(partnerId);
        Long repeatCustomers = deliveryRepository.countRepeatCustomersByPartnerId(partnerId);
        
        return PartnerAnalyticsSummaryDTO.builder()
                .totalOrdersToday(totalOrdersToday)
                .totalOrdersThisWeek(totalOrdersThisWeek)
                .totalOrdersThisMonth(totalOrdersThisMonth)
                .completedOrdersToday(completedOrdersToday)
                .completedOrdersThisWeek(completedOrdersThisWeek)
                .completedOrdersThisMonth(completedOrdersThisMonth)
                .totalBillingToday(totalBillingToday != null ? totalBillingToday : BigDecimal.ZERO)
                .totalBillingThisWeek(totalBillingThisWeek != null ? totalBillingThisWeek : BigDecimal.ZERO)
                .totalBillingThisMonth(totalBillingThisMonth != null ? totalBillingThisMonth : BigDecimal.ZERO)
                .pendingBillingAmount(pendingBillingAmount != null ? pendingBillingAmount : BigDecimal.ZERO)
                .paidBillingAmount(paidBillingAmount != null ? paidBillingAmount : BigDecimal.ZERO)
                .totalInventoryItems(totalInventoryItems)
                .lowStockItems(lowStockItems)
                .outOfStockItems(outOfStockItems)
                .expiredItems(expiredItems)
                .totalInventoryValue(totalInventoryValue != null ? totalInventoryValue : BigDecimal.ZERO)
                .averageOrderValue(averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO)
                .totalCustomers(totalCustomers)
                .repeatCustomers(repeatCustomers)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
    }
    
    // Geospatial Analytics
    public GeospatialAnalyticsDTO getGeospatialAnalytics(LocalDate fromDate, LocalDate toDate) {
        log.info("Generating geospatial analytics from {} to {}", fromDate, toDate);
        
        Instant startDate = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = toDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        // TODO: Implement geospatial analytics when address entities are available
        // Get delivery data by region
        // List<Object[]> regionDeliveryData = deliveryRepository.findDeliveryStatsByRegionAndDateRange(startDate, endDate);
        List<GeospatialAnalyticsDTO.RegionDeliveryData> regionData = new ArrayList<>();
        
        // Get delivery data by woreda
        // List<Object[]> woredaDeliveryData = deliveryRepository.findDeliveryStatsByWoredaAndDateRange(startDate, endDate);
        List<GeospatialAnalyticsDTO.WoredaDeliveryData> woredaData = new ArrayList<>();
        
        // Calculate totals
        BigDecimal totalDeliveries = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        return GeospatialAnalyticsDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .regionData(regionData)
                .woredaData(woredaData)
                .totalDeliveries(totalDeliveries)
                .totalRevenue(totalRevenue)
                .build();
    }
    
    // Compliance Report
    public ComplianceReportDTO getComplianceReport(LocalDate fromDate, LocalDate toDate) {
        log.info("Generating compliance report from {} to {}", fromDate, toDate);
        
        Instant startDate = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDate = toDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        // Driver compliance data
        List<Object[]> driverComplianceData = deliveryRepository.findDriverComplianceData(startDate, endDate);
        List<ComplianceReportDTO.DriverComplianceData> driverCompliance = driverComplianceData.stream()
                .map(row -> {
                    Long totalDeliveries = (Long) row[3];
                    Long missedDeliveries = (Long) row[4];
                    Long lateDeliveries = (Long) row[5];
                    Long onTimeDeliveries = totalDeliveries - missedDeliveries - lateDeliveries;
                    
                    BigDecimal complianceScore = calculateDriverComplianceScore(totalDeliveries, missedDeliveries, lateDeliveries);
                    String status = getComplianceStatus(complianceScore);
                    
                    return ComplianceReportDTO.DriverComplianceData.builder()
                            .driverId((Long) row[0])
                            .driverName((String) row[1])
                            .phoneNumber((String) row[2])
                            .totalDeliveries(totalDeliveries)
                            .missedDeliveries(missedDeliveries)
                            .lateDeliveries(lateDeliveries)
                            .onTimeDeliveries(onTimeDeliveries)
                            .complianceScore(complianceScore)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Partner compliance data
        List<Object[]> partnerComplianceData = partnerRepository.findPartnerComplianceData();
        List<ComplianceReportDTO.PartnerComplianceData> partnerCompliance = partnerComplianceData.stream()
                .map(row -> {
                    Long totalOrders = (Long) row[3];
                    Long expiredItems = (Long) row[4];
                    Long lowStockItems = (Long) row[5];
                    Boolean kycCompleted = (Boolean) row[6];
                    
                    BigDecimal complianceScore = calculatePartnerComplianceScore(totalOrders, expiredItems, lowStockItems, kycCompleted);
                    String status = getComplianceStatus(complianceScore);
                    
                    return ComplianceReportDTO.PartnerComplianceData.builder()
                            .partnerId((Long) row[0])
                            .partnerName((String) row[1])
                            .businessName((String) row[2])
                            .totalOrders(totalOrders)
                            .expiredInventoryItems(expiredItems)
                            .lowStockItems(lowStockItems)
                            .kycCompleted(kycCompleted)
                            .complianceScore(complianceScore)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Calculate summary
        Long totalDrivers = Long.valueOf(driverCompliance.size());
        Long compliantDrivers = driverCompliance.stream()
                .filter(d -> d.getComplianceScore().compareTo(BigDecimal.valueOf(70)) >= 0)
                .count();
        
        BigDecimal averageDriverScore = driverCompliance.isEmpty() ? BigDecimal.ZERO :
                driverCompliance.stream()
                        .map(ComplianceReportDTO.DriverComplianceData::getComplianceScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(driverCompliance.size()), 2, RoundingMode.HALF_UP);
        
        Long totalPartners = Long.valueOf(partnerCompliance.size());
        Long compliantPartners = partnerCompliance.stream()
                .filter(p -> p.getComplianceScore().compareTo(BigDecimal.valueOf(70)) >= 0)
                .count();
        
        BigDecimal averagePartnerScore = partnerCompliance.isEmpty() ? BigDecimal.ZERO :
                partnerCompliance.stream()
                        .map(ComplianceReportDTO.PartnerComplianceData::getComplianceScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(partnerCompliance.size()), 2, RoundingMode.HALF_UP);
        
        ComplianceReportDTO.ComplianceSummary summary = ComplianceReportDTO.ComplianceSummary.builder()
                .totalDrivers(totalDrivers)
                .compliantDrivers(compliantDrivers)
                .averageDriverScore(averageDriverScore)
                .totalPartners(totalPartners)
                .compliantPartners(compliantPartners)
                .averagePartnerScore(averagePartnerScore)
                .build();
        
        return ComplianceReportDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .driverCompliance(driverCompliance)
                .partnerCompliance(partnerCompliance)
                .summary(summary)
                .build();
    }
    
    // Helper methods
    private BigDecimal calculateDriverComplianceScore(Long totalDeliveries, Long missedDeliveries, Long lateDeliveries) {
        if (totalDeliveries == 0) return BigDecimal.ZERO;
        
        Long onTimeDeliveries = totalDeliveries - missedDeliveries - lateDeliveries;
        double score = (double) onTimeDeliveries / totalDeliveries * 100;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculatePartnerComplianceScore(Long totalOrders, Long expiredItems, Long lowStockItems, Boolean kycCompleted) {
        BigDecimal score = BigDecimal.valueOf(100);
        
        // Deduct points for expired items (10 points per expired item, max 30 points)
        if (expiredItems > 0) {
            BigDecimal expiredDeduction = BigDecimal.valueOf(Math.min(expiredItems * 10, 30));
            score = score.subtract(expiredDeduction);
        }
        
        // Deduct points for low stock items (5 points per low stock item, max 20 points)
        if (lowStockItems > 0) {
            BigDecimal lowStockDeduction = BigDecimal.valueOf(Math.min(lowStockItems * 5, 20));
            score = score.subtract(lowStockDeduction);
        }
        
        // Deduct points for incomplete KYC (20 points)
        if (kycCompleted == null || !kycCompleted) {
            score = score.subtract(BigDecimal.valueOf(20));
        }
        
        return score.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
    
    private String getComplianceStatus(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "EXCELLENT";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "GOOD";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "FAIR";
        return "POOR";
    }
}
