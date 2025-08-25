package org.driver.driverapp.repository;

import org.driver.driverapp.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find by user ID
    Optional<Customer> findByUserId(Long userId);

    // Find by phone number
    Optional<Customer> findByPhone(String phone);

    // Find by email
    Optional<Customer> findByEmail(String email);

    // Find by phone or email
    Optional<Customer> findByPhoneOrEmail(String phone, String email);

    // Check if phone exists
    boolean existsByPhone(String phone);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if phone or email exists
    boolean existsByPhoneOrEmail(String phone, String email);

    // Find all active customers
    Page<Customer> findByActiveTrue(Pageable pageable);

    // Find customers by region
    Page<Customer> findByRegion(String region, Pageable pageable);

    // Find customers by region and active status
    Page<Customer> findByRegionAndActiveTrue(String region, Pageable pageable);

    // Find verified customers
    Page<Customer> findByVerifiedTrue(Pageable pageable);

    // Find active and verified customers
    Page<Customer> findByActiveTrueAndVerifiedTrue(Pageable pageable);

    // Search by full name (case-insensitive)
    Page<Customer> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    // Search by phone number (partial match)
    Page<Customer> findByPhoneContaining(String phone, Pageable pageable);

    // Search by email (partial match, case-insensitive)
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // Search by full name or phone or email
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.phone LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchByFullNameOrPhoneOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find customers by preferred payment method
    Page<Customer> findByPreferredPayment(String preferredPayment, Pageable pageable);

    // Count customers by region
    long countByRegion(String region);

    // Count active customers by region
    long countByRegionAndActiveTrue(String region);

    // Count verified customers
    long countByVerifiedTrue();

    // Count active customers
    long countByActiveTrue();

    // Find customers by region with delivery preferences
    @Query("SELECT c FROM Customer c WHERE c.region = :region AND c.deliveryPreferences IS NOT NULL")
    Page<Customer> findByRegionWithDeliveryPreferences(@Param("region") String region, Pageable pageable);
    
    // Analytics methods
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate AND c.active = true")
    Long countByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
