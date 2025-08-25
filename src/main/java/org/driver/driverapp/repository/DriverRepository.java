package org.driver.driverapp.repository;


import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.user.username = :username")
    Optional<Driver> findByUserUsername(String username);

    Optional<Driver> findByPhoneNumber(String phoneNumber);

    List<Driver> findByNameContainingIgnoreCase(String name);

    List<Driver> findByPhoneNumberContaining(String phoneNumber);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByNameContainingIgnoreCaseAndStatus(String name, DriverStatus status);

    @Query("""
        SELECT d FROM Driver d
        WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR d.status = :status)
        AND (:phone IS NULL OR d.phoneNumber LIKE %:phone%)
        AND (:vehicleType IS NULL OR d.vehicleType = :vehicleType)
        AND (:isOnline IS NULL OR d.isOnline = :isOnline)
        AND (:active IS NULL OR d.active = :active)
    """)
    List<Driver> searchDrivers(
            @org.springframework.lang.Nullable String name,
            @org.springframework.lang.Nullable DriverStatus status,
            @org.springframework.lang.Nullable String phone,
            @org.springframework.lang.Nullable String vehicleType,
            @org.springframework.lang.Nullable Boolean isOnline,
            @org.springframework.lang.Nullable Boolean active
    );
    
    // Analytics methods
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.status = :status AND d.lastLoginAt BETWEEN :startDate AND :endDate AND d.active = true")
    Long countByStatusAndLastActiveAtBetween(@Param("status") DriverStatus status,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.status = :status AND d.active = true")
    Long countByStatus(@Param("status") DriverStatus status);
}

