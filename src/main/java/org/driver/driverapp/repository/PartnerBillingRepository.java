package org.driver.driverapp.repository;

import org.driver.driverapp.enums.PaymentStatus;
import org.driver.driverapp.model.PartnerBilling;
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
public interface PartnerBillingRepository extends JpaRepository<PartnerBilling, Long> {

    // Find by partner
    Page<PartnerBilling> findByPartnerIdAndActiveTrue(Long partnerId, Pageable pageable);
    
    List<PartnerBilling> findByPartnerIdAndStatusAndActiveTrue(Long partnerId, PaymentStatus status);
    
    // Find by invoice
    List<PartnerBilling> findByInvoiceIdAndActiveTrue(Long invoiceId);
    
    Optional<PartnerBilling> findByInvoiceIdAndStatusAndActiveTrue(Long invoiceId, PaymentStatus status);
    
    // Find by status
    Page<PartnerBilling> findByStatusAndActiveTrue(PaymentStatus status, Pageable pageable);
    
    List<PartnerBilling> findByStatusAndActiveTrue(PaymentStatus status);
    
    // Find by payment reference
    Optional<PartnerBilling> findByPaymentReferenceAndActiveTrue(String paymentReference);
    
    // Find by date range
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.createdAt BETWEEN :startDate AND :endDate AND pb.active = true")
    List<PartnerBilling> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find by partner and date range
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.partner.id = :partnerId AND pb.createdAt BETWEEN :startDate AND :endDate AND pb.active = true")
    List<PartnerBilling> findByPartnerIdAndCreatedAtBetween(@Param("partnerId") Long partnerId,
                                                           @Param("startDate") Instant startDate,
                                                           @Param("endDate") Instant endDate);
    
    // Find by status and date range
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.status = :status AND pb.createdAt BETWEEN :startDate AND :endDate AND pb.active = true")
    List<PartnerBilling> findByStatusAndCreatedAtBetween(@Param("status") PaymentStatus status,
                                                        @Param("startDate") Instant startDate,
                                                        @Param("endDate") Instant endDate);
    
    // Count by status
    long countByStatusAndActiveTrue(PaymentStatus status);
    
    // Count by partner
    long countByPartnerIdAndActiveTrue(Long partnerId);
    
    // Sum amounts by status
    @Query("SELECT COALESCE(SUM(pb.amount), 0) FROM PartnerBilling pb WHERE pb.status = :status AND pb.active = true")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
    
    // Sum amounts by partner
    @Query("SELECT COALESCE(SUM(pb.amount), 0) FROM PartnerBilling pb WHERE pb.partner.id = :partnerId AND pb.active = true")
    BigDecimal sumAmountByPartnerId(@Param("partnerId") Long partnerId);
    
    // Sum amounts by partner and status
    @Query("SELECT COALESCE(SUM(pb.amount), 0) FROM PartnerBilling pb WHERE pb.partner.id = :partnerId AND pb.status = :status AND pb.active = true")
    BigDecimal sumAmountByPartnerIdAndStatus(@Param("partnerId") Long partnerId, @Param("status") PaymentStatus status);
    
    // Find pending billings
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.status = 'PENDING' AND pb.active = true ORDER BY pb.createdAt ASC")
    List<PartnerBilling> findPendingBillings();
    
    // Find failed billings
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.status = 'FAILED' AND pb.active = true ORDER BY pb.createdAt DESC")
    List<PartnerBilling> findFailedBillings();
    
    // Find completed billings by date range
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.status = 'COMPLETED' AND pb.paymentDate BETWEEN :startDate AND :endDate AND pb.active = true")
    List<PartnerBilling> findCompletedBillingsByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    // Find billings by amount range
    @Query("SELECT pb FROM PartnerBilling pb WHERE pb.amount BETWEEN :minAmount AND :maxAmount AND pb.active = true")
    List<PartnerBilling> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    // Find top billing partners
    @Query("SELECT pb.partner.id, SUM(pb.amount) as totalBilling FROM PartnerBilling pb " +
           "WHERE pb.active = true GROUP BY pb.partner.id ORDER BY totalBilling DESC")
    List<Object[]> findTopBillingPartners();
}
