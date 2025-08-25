package org.driver.driverapp.repository;

import org.driver.driverapp.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByName(String name);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
    
    Optional<Partner> findByPhone(String phone);
    
    // Analytics methods
    @Query("SELECT COUNT(p) FROM Partner p WHERE p.active = true")
    Long countByActiveTrue();
    
    // Simplified query without problematic subqueries
    @Query("SELECT p.id, p.name, p.verified FROM Partner p WHERE p.active = true")
    List<Object[]> findPartnerComplianceData();
}
