package org.driver.driverapp.repository;

import org.driver.driverapp.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Find by partner (location)
    List<Supplier> findByPartnerId(Long partnerId);
    
    Page<Supplier> findByPartnerId(Long partnerId, Pageable pageable);

    // Find active suppliers
    List<Supplier> findByActiveTrue();
    
    Page<Supplier> findByActiveTrue(Pageable pageable);

    // Find by partner and active status
    List<Supplier> findByPartnerIdAndActiveTrue(Long partnerId);
    
    Page<Supplier> findByPartnerIdAndActiveTrue(Long partnerId, Pageable pageable);

    // Find by name (case-insensitive)
    Optional<Supplier> findByNameIgnoreCase(String name);

    // Find by phone
    Optional<Supplier> findByPhone(String phone);

    // Find by email
    Optional<Supplier> findByEmail(String email);

    // Find verified suppliers
    List<Supplier> findByVerifiedTrue();
    
    Page<Supplier> findByVerifiedTrue(Pageable pageable);

    // Find by region
    List<Supplier> findByRegion(String region);
    
    Page<Supplier> findByRegion(String region, Pageable pageable);

    // Find by city
    List<Supplier> findByCity(String city);
    
    Page<Supplier> findByCity(String city, Pageable pageable);

    // Search suppliers by name containing
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Supplier> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count suppliers by partner
    long countByPartnerId(Long partnerId);

    // Count active suppliers
    long countByActiveTrue();

    // Count verified suppliers
    long countByVerifiedTrue();

    // Check if supplier exists by name
    boolean existsByNameIgnoreCase(String name);

    // Check if supplier exists by phone
    boolean existsByPhone(String phone);

    // Check if supplier exists by email
    boolean existsByEmail(String email);
}
