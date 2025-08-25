package org.driver.driverapp.repository;

import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.model.PostalCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostalCodeRepository extends JpaRepository<PostalCode, Long> {

    // Find by region
    List<PostalCode> findByRegionAndActiveTrue(EthiopianRegion region);
    
    // Find by code
    Optional<PostalCode> findByCodeAndActiveTrue(String code);
    
    // Find by region and code
    Optional<PostalCode> findByRegionAndCodeAndActiveTrue(EthiopianRegion region, String code);
    
    // Find all active postal codes
    List<PostalCode> findByActiveTrue();
    
    // Find all active postal codes with pagination
    Page<PostalCode> findByActiveTrue(Pageable pageable);
    
    // Find by region with pagination
    Page<PostalCode> findByRegionAndActiveTrue(EthiopianRegion region, Pageable pageable);
    
    // Search by code pattern
    @Query("SELECT pc FROM PostalCode pc WHERE pc.active = true AND pc.code LIKE %:codePattern%")
    List<PostalCode> findByCodePattern(@Param("codePattern") String codePattern);
    
    // Search by region and code pattern
    @Query("SELECT pc FROM PostalCode pc WHERE pc.active = true AND pc.region = :region AND pc.code LIKE %:codePattern%")
    List<PostalCode> findByRegionAndCodePattern(@Param("region") EthiopianRegion region, @Param("codePattern") String codePattern);
    
    // Count by region
    long countByRegionAndActiveTrue(EthiopianRegion region);
    
    // Check if code exists
    boolean existsByCodeAndActiveTrue(String code);
    
    // Check if code exists in region
    boolean existsByRegionAndCodeAndActiveTrue(EthiopianRegion region, String code);
}
