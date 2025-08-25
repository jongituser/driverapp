package org.driver.driverapp.repository;

import org.driver.driverapp.enums.InvoiceStatus;
import org.driver.driverapp.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Find by partner
    Page<Invoice> findByPartnerIdAndActiveTrue(Long partnerId, Pageable pageable);
    
    List<Invoice> findByPartnerIdAndStatusAndActiveTrue(Long partnerId, InvoiceStatus status);
    
    // Find by status
    Page<Invoice> findByStatusAndActiveTrue(InvoiceStatus status, Pageable pageable);
    
    List<Invoice> findByStatusAndActiveTrue(InvoiceStatus status);
    
    // Find by invoice number
    Optional<Invoice> findByInvoiceNumberAndActiveTrue(String invoiceNumber);
    
    // Find by due date
    List<Invoice> findByDueDateAndActiveTrue(LocalDate dueDate);
    
    List<Invoice> findByDueDateBeforeAndStatusNotAndActiveTrue(LocalDate dueDate, InvoiceStatus status);
    
    // Find overdue invoices
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status NOT IN ('PAID', 'CANCELLED') AND i.active = true")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);
    
    // Find by date range
    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate AND i.active = true")
    List<Invoice> findByCreatedAtBetween(@Param("startDate") java.time.Instant startDate, 
                                        @Param("endDate") java.time.Instant endDate);
    
    // Find by partner and date range
    @Query("SELECT i FROM Invoice i WHERE i.partner.id = :partnerId AND i.createdAt BETWEEN :startDate AND :endDate AND i.active = true")
    List<Invoice> findByPartnerIdAndCreatedAtBetween(@Param("partnerId") Long partnerId,
                                                     @Param("startDate") java.time.Instant startDate,
                                                     @Param("endDate") java.time.Instant endDate);
    
    // Count by status
    long countByStatusAndActiveTrue(InvoiceStatus status);
    
    // Count by partner
    long countByPartnerIdAndActiveTrue(Long partnerId);
    
    // Sum amounts by status
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = :status AND i.active = true")
    BigDecimal sumAmountByStatus(@Param("status") InvoiceStatus status);
    
    // Sum amounts by partner
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.partner.id = :partnerId AND i.active = true")
    BigDecimal sumAmountByPartnerId(@Param("partnerId") Long partnerId);
    
    // Sum paid amounts by partner
    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i WHERE i.partner.id = :partnerId AND i.active = true")
    BigDecimal sumPaidAmountByPartnerId(@Param("partnerId") Long partnerId);
    
    // Find unpaid invoices
    @Query("SELECT i FROM Invoice i WHERE i.status NOT IN ('PAID', 'CANCELLED') AND i.active = true")
    List<Invoice> findUnpaidInvoices();
    
    // Find invoices due soon (within next 7 days)
    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :today AND :nextWeek AND i.status NOT IN ('PAID', 'CANCELLED') AND i.active = true")
    List<Invoice> findInvoicesDueSoon(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);
    
    // Find invoices by amount range
    @Query("SELECT i FROM Invoice i WHERE i.totalAmount BETWEEN :minAmount AND :maxAmount AND i.active = true")
    List<Invoice> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
}
