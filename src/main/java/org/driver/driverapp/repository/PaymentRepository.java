package org.driver.driverapp.repository;

import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;
import org.driver.driverapp.model.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by user
    Page<Payment> findByUserIdAndActiveTrue(Long userId, Pageable pageable);
    
    List<Payment> findByUserIdAndStatusAndActiveTrue(Long userId, PaymentStatus status);
    
    // Find by delivery
    List<Payment> findByDeliveryIdAndActiveTrue(Long deliveryId);
    
    Optional<Payment> findByDeliveryIdAndStatusAndActiveTrue(Long deliveryId, PaymentStatus status);
    
    // Find by status
    Page<Payment> findByStatusAndActiveTrue(PaymentStatus status, Pageable pageable);
    
    List<Payment> findByStatusAndActiveTrue(PaymentStatus status);
    
    // Find by provider
    Page<Payment> findByProviderAndActiveTrue(PaymentProvider provider, Pageable pageable);
    
    // Find by transaction reference
    Optional<Payment> findByTransactionRefAndActiveTrue(String transactionRef);
    
    // Find by date range
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.active = true")
    List<Payment> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find by user and date range
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.createdAt BETWEEN :startDate AND :endDate AND p.active = true")
    List<Payment> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                 @Param("startDate") Instant startDate, 
                                                 @Param("endDate") Instant endDate);
    
    // Find by status and date range
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate AND p.active = true")
    List<Payment> findByStatusAndCreatedAtBetween(@Param("status") PaymentStatus status,
                                                 @Param("startDate") Instant startDate,
                                                 @Param("endDate") Instant endDate);
    
    // Count by status
    long countByStatusAndActiveTrue(PaymentStatus status);
    
    // Count by provider
    long countByProviderAndActiveTrue(PaymentProvider provider);
    
    // Sum amounts by status
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.active = true")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
    
    // Sum amounts by provider
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.provider = :provider AND p.active = true")
    BigDecimal sumAmountByProvider(@Param("provider") PaymentProvider provider);
    
    // Sum amounts by user
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user.id = :userId AND p.active = true")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);
    
    // Sum amounts by user and status
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user.id = :userId AND p.status = :status AND p.active = true")
    BigDecimal sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);
    
    // Find failed payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.active = true ORDER BY p.createdAt DESC")
    List<Payment> findFailedPayments();
    
    // Find pending payments older than specified time
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime AND p.active = true")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") Instant cutoffTime);
    
    // Analytics methods
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate AND p.active = true")
    BigDecimal sumAmountByStatusAndCreatedAtBetween(@Param("status") String status,
                                                   @Param("startDate") Instant startDate,
                                                   @Param("endDate") Instant endDate);
    
    @Query("SELECT COALESCE(AVG(p.amount), 0) FROM Payment p WHERE p.active = true")
    BigDecimal findAverageOrderValue();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.delivery.partner.id = :partnerId AND p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate AND p.active = true")
    BigDecimal sumAmountByPartnerIdAndStatusAndCreatedAtBetween(@Param("partnerId") Long partnerId,
                                                               @Param("status") String status,
                                                               @Param("startDate") Instant startDate,
                                                               @Param("endDate") Instant endDate);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.delivery.partner.id = :partnerId AND p.status = :status AND p.active = true")
    BigDecimal sumAmountByPartnerIdAndStatus(@Param("partnerId") Long partnerId, @Param("status") String status);
    
    @Query("SELECT COALESCE(AVG(p.amount), 0) FROM Payment p WHERE p.delivery.partner.id = :partnerId AND p.active = true")
    BigDecimal findAverageOrderValueByPartnerId(@Param("partnerId") Long partnerId);
}
