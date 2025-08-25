package org.driver.driverapp.repository;

import org.driver.driverapp.enums.AnalyticsRecordType;
import org.driver.driverapp.model.AnalyticsRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnalyticsRecordRepository extends JpaRepository<AnalyticsRecord, Long> {
    
    List<AnalyticsRecord> findByTypeAndActiveTrue(AnalyticsRecordType type);
    
    List<AnalyticsRecord> findByTypeAndEntityIdAndActiveTrue(AnalyticsRecordType type, Long entityId);
    
    Page<AnalyticsRecord> findByTypeAndActiveTrue(AnalyticsRecordType type, Pageable pageable);
    
    @Query("SELECT ar FROM AnalyticsRecord ar WHERE ar.type = :type AND ar.createdAt BETWEEN :startDate AND :endDate AND ar.active = true")
    List<AnalyticsRecord> findByTypeAndDateRange(
            @Param("type") AnalyticsRecordType type,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
    
    @Query("SELECT ar FROM AnalyticsRecord ar WHERE ar.createdAt BETWEEN :startDate AND :endDate AND ar.active = true")
    List<AnalyticsRecord> findByDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
    
    @Query("SELECT COUNT(ar) FROM AnalyticsRecord ar WHERE ar.type = :type AND ar.createdAt BETWEEN :startDate AND :endDate AND ar.active = true")
    Long countByTypeAndDateRange(
            @Param("type") AnalyticsRecordType type,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}

