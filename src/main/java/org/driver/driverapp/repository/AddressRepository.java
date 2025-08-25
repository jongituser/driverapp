package org.driver.driverapp.repository;

import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Find by customer
    List<Address> findByCustomerIdAndActiveTrue(Long customerId);
    
    // Find by partner
    List<Address> findByPartnerIdAndActiveTrue(Long partnerId);
    
    // Find by customer with pagination
    Page<Address> findByCustomerIdAndActiveTrue(Long customerId, Pageable pageable);
    
    // Find by partner with pagination
    Page<Address> findByPartnerIdAndActiveTrue(Long partnerId, Pageable pageable);
    
    // Find by region
    List<Address> findByRegionAndActiveTrue(EthiopianRegion region);
    
    // Find by woreda
    List<Address> findByWoredaAndActiveTrue(String woreda);
    
    // Find by kebele
    List<Address> findByKebeleAndActiveTrue(String kebele);
    
    // Find by region and woreda
    List<Address> findByRegionAndWoredaAndActiveTrue(EthiopianRegion region, String woreda);
    
    // Find by region, woreda, and kebele
    List<Address> findByRegionAndWoredaAndKebeleAndActiveTrue(EthiopianRegion region, String woreda, String kebele);
    
    // Find by postal code
    List<Address> findByPostalCodeIdAndActiveTrue(Long postalCodeId);
    
    // Find addresses within GPS radius
    @Query("SELECT a FROM Address a WHERE a.active = true AND " +
           "SQRT(POWER(a.gpsLat - :lat, 2) + POWER(a.gpsLong - :lng, 2)) <= :radius")
    List<Address> findAddressesWithinRadius(@Param("lat") BigDecimal lat, 
                                           @Param("lng") BigDecimal lng, 
                                           @Param("radius") BigDecimal radius);
    
    // Find addresses by GPS coordinates (exact match)
    Optional<Address> findByGpsLatAndGpsLongAndActiveTrue(BigDecimal gpsLat, BigDecimal gpsLong);
    
    // Find addresses by GPS coordinates (approximate match)
    @Query("SELECT a FROM Address a WHERE a.active = true AND " +
           "ABS(a.gpsLat - :lat) <= :tolerance AND ABS(a.gpsLong - :lng) <= :tolerance")
    List<Address> findByGpsCoordinatesApproximate(@Param("lat") BigDecimal lat, 
                                                 @Param("lng") BigDecimal lng, 
                                                 @Param("tolerance") BigDecimal tolerance);
    
    // Find GPS-only addresses
    @Query("SELECT a FROM Address a WHERE a.active = true AND a.region IS NULL AND a.woreda IS NULL AND a.kebele IS NULL")
    List<Address> findGpsOnlyAddresses();
    
    // Find full Ethiopian addresses
    @Query("SELECT a FROM Address a WHERE a.active = true AND a.region IS NOT NULL AND a.woreda IS NOT NULL AND a.kebele IS NOT NULL")
    List<Address> findFullEthiopianAddresses();
    
    // Find hybrid addresses (GPS + Ethiopian)
    @Query("SELECT a FROM Address a WHERE a.active = true AND a.region IS NOT NULL AND a.woreda IS NOT NULL AND a.kebele IS NOT NULL AND a.gpsLat IS NOT NULL AND a.gpsLong IS NOT NULL")
    List<Address> findHybridAddresses();
    
    // Count by customer
    long countByCustomerIdAndActiveTrue(Long customerId);
    
    // Count by partner
    long countByPartnerIdAndActiveTrue(Long partnerId);
    
    // Count by region
    long countByRegionAndActiveTrue(EthiopianRegion region);
    
    // Check if customer has addresses
    boolean existsByCustomerIdAndActiveTrue(Long customerId);
    
    // Check if partner has addresses
    boolean existsByPartnerIdAndActiveTrue(Long partnerId);
    
    // Find addresses by description pattern
    @Query("SELECT a FROM Address a WHERE a.active = true AND a.description LIKE %:descriptionPattern%")
    List<Address> findByDescriptionPattern(@Param("descriptionPattern") String descriptionPattern);
}
