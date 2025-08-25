package org.driver.driverapp.repository;

import org.driver.driverapp.enums.PayoutStatus;
import org.driver.driverapp.model.DriverEarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverEarningRepository extends JpaRepository<DriverEarning, Long> {

    // Find by driver
    Page<DriverEarning> findByDriverIdAndActiveTrue(Long driverId, Pageable pageable);
    
    List<DriverEarning> findByDriverIdAndPayoutStatusAndActiveTrue(Long driverId, PayoutStatus payoutStatus);
    
    // Find by delivery
    Optional<DriverEarning> findByDeliveryIdAndActiveTrue(Long deliveryId);
    
    // Find by payout status
    Page<DriverEarning> findByPayoutStatusAndActiveTrue(PayoutStatus payoutStatus, Pageable pageable);
    
    List<DriverEarning> findByPayoutStatusAndActiveTrue(PayoutStatus payoutStatus);
    
    // Find by payout reference
    Optional<DriverEarning> findByPayoutReferenceAndActiveTrue(String payoutReference);
    
    // Find by date range
    @Query("SELECT de FROM DriverEarning de WHERE de.createdAt BETWEEN :startDate AND :endDate AND de.active = true")
    List<DriverEarning> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find by driver and date range
    @Query("SELECT de FROM DriverEarning de WHERE de.driver.id = :driverId AND de.createdAt BETWEEN :startDate AND :endDate AND de.active = true")
    List<DriverEarning> findByDriverIdAndCreatedAtBetween(@Param("driverId") Long driverId,
                                                         @Param("startDate") Instant startDate,
                                                         @Param("endDate") Instant endDate);
    
    // Find by payout status and date range
    @Query("SELECT de FROM DriverEarning de WHERE de.payoutStatus = :payoutStatus AND de.createdAt BETWEEN :startDate AND :endDate AND de.active = true")
    List<DriverEarning> findByPayoutStatusAndCreatedAtBetween(@Param("payoutStatus") PayoutStatus payoutStatus,
                                                             @Param("startDate") Instant startDate,
                                                             @Param("endDate") Instant endDate);
    
    // Count by payout status
    long countByPayoutStatusAndActiveTrue(PayoutStatus payoutStatus);
    
    // Count by driver
    long countByDriverIdAndActiveTrue(Long driverId);
    
    // Sum amounts by payout status
    @Query("SELECT COALESCE(SUM(de.amount), 0) FROM DriverEarning de WHERE de.payoutStatus = :payoutStatus AND de.active = true")
    BigDecimal sumAmountByPayoutStatus(@Param("payoutStatus") PayoutStatus payoutStatus);
    
    // Sum amounts by driver
    @Query("SELECT COALESCE(SUM(de.amount), 0) FROM DriverEarning de WHERE de.driver.id = :driverId AND de.active = true")
    BigDecimal sumAmountByDriverId(@Param("driverId") Long driverId);
    
    // Sum amounts by driver and payout status
    @Query("SELECT COALESCE(SUM(de.amount), 0) FROM DriverEarning de WHERE de.driver.id = :driverId AND de.payoutStatus = :payoutStatus AND de.active = true")
    BigDecimal sumAmountByDriverIdAndPayoutStatus(@Param("driverId") Long driverId, @Param("payoutStatus") PayoutStatus payoutStatus);
    
    // Find pending payouts
    @Query("SELECT de FROM DriverEarning de WHERE de.payoutStatus = 'PENDING' AND de.active = true ORDER BY de.createdAt ASC")
    List<DriverEarning> findPendingPayouts();
    
    // Find failed payouts
    @Query("SELECT de FROM DriverEarning de WHERE de.payoutStatus = 'FAILED' AND de.active = true ORDER BY de.createdAt DESC")
    List<DriverEarning> findFailedPayouts();
    
    // Find completed payouts by date range
    @Query("SELECT de FROM DriverEarning de WHERE de.payoutStatus = 'COMPLETED' AND de.payoutDate BETWEEN :startDate AND :endDate AND de.active = true")
    List<DriverEarning> findCompletedPayoutsByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find earnings by amount range
    @Query("SELECT de FROM DriverEarning de WHERE de.amount BETWEEN :minAmount AND :maxAmount AND de.active = true")
    List<DriverEarning> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    // Find top earning drivers
    @Query("SELECT de.driver.id, SUM(de.amount) as totalEarnings FROM DriverEarning de " +
           "WHERE de.active = true GROUP BY de.driver.id ORDER BY totalEarnings DESC")
    List<Object[]> findTopEarningDrivers();
}
