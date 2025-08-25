package org.driver.driverapp.repository;

import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    @Query("SELECT d.id FROM Delivery d JOIN d.customer c WHERE c.phone = :phoneNumber")
    Optional<Long> findDeliveryIdByCustomerPhone(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT d.id FROM Delivery d JOIN d.driver dr WHERE dr.phoneNumber = :phoneNumber")
    Optional<Long> findDeliveryIdByDriverPhone(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT d.id FROM Delivery d JOIN d.partner p WHERE p.phone = :phoneNumber")
    Optional<Long> findDeliveryIdByPartnerPhone(@Param("phoneNumber") String phoneNumber);
    
    // Analytics methods
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.status = :status AND d.createdAt BETWEEN :startDate AND :endDate")
    Long countByStatusAndCreatedAtBetween(@Param("status") DeliveryStatus status, 
                                         @Param("startDate") Instant startDate, 
                                         @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.partner.id = :partnerId AND d.createdAt BETWEEN :startDate AND :endDate")
    Long countByPartnerIdAndCreatedAtBetween(@Param("partnerId") Long partnerId,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.partner.id = :partnerId AND d.status = :status AND d.createdAt BETWEEN :startDate AND :endDate")
    Long countByPartnerIdAndStatusAndCreatedAtBetween(@Param("partnerId") Long partnerId,
                                                     @Param("status") DeliveryStatus status,
                                                     @Param("startDate") Instant startDate,
                                                     @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(DISTINCT d.customer.id) FROM Delivery d WHERE d.partner.id = :partnerId")
    Long countDistinctCustomerIdByPartnerId(@Param("partnerId") Long partnerId);
    
    @Query("SELECT COUNT(DISTINCT d.customer.id) FROM Delivery d WHERE d.partner.id = :partnerId " +
           "AND d.customer.id IN (SELECT d2.customer.id FROM Delivery d2 WHERE d2.partner.id = :partnerId GROUP BY d2.customer.id HAVING COUNT(d2) > 1)")
    Long countRepeatCustomersByPartnerId(@Param("partnerId") Long partnerId);
    
    // Compliance analytics
    @Query("SELECT d.driver.id, d.driver.name, d.driver.phoneNumber, " +
           "COUNT(d), " +
           "SUM(CASE WHEN d.status = 'DELIVERY_FAILED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN d.status = 'DELAYED' THEN 1 ELSE 0 END) " +
           "FROM Delivery d " +
           "WHERE d.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY d.driver.id, d.driver.name, d.driver.phoneNumber")
    List<Object[]> findDriverComplianceData(@Param("startDate") Instant startDate, 
                                           @Param("endDate") Instant endDate);
}
