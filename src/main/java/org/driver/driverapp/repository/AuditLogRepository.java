package org.driver.driverapp.repository;

import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    // Find by entity type and ID
    List<AuditLog> findByEntityTypeAndEntityIdAndActiveTrueOrderByCreatedAtDesc(
            AuditEntityType entityType, Long entityId);
    
    // Find by user
    Page<AuditLog> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find by action
    Page<AuditLog> findByActionAndActiveTrueOrderByCreatedAtDesc(AuditAction action, Pageable pageable);
    
    // Find by entity type
    Page<AuditLog> findByEntityTypeAndActiveTrueOrderByCreatedAtDesc(AuditEntityType entityType, Pageable pageable);
    
    // Find by date range
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate AND al.active = true ORDER BY al.createdAt DESC")
    Page<AuditLog> findByCreatedAtBetween(@Param("startDate") Instant startDate, 
                                         @Param("endDate") Instant endDate, 
                                         Pageable pageable);
    
    // Find by user and date range
    @Query("SELECT al FROM AuditLog al WHERE al.userId = :userId AND al.createdAt BETWEEN :startDate AND :endDate AND al.active = true ORDER BY al.createdAt DESC")
    Page<AuditLog> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                  @Param("startDate") Instant startDate,
                                                  @Param("endDate") Instant endDate,
                                                  Pageable pageable);
    
    // Find by entity type and date range
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.createdAt BETWEEN :startDate AND :endDate AND al.active = true ORDER BY al.createdAt DESC")
    Page<AuditLog> findByEntityTypeAndCreatedAtBetween(@Param("entityType") AuditEntityType entityType,
                                                      @Param("startDate") Instant startDate,
                                                      @Param("endDate") Instant endDate,
                                                      Pageable pageable);
    
    // Count by entity type and action
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.entityType = :entityType AND al.action = :action AND al.active = true")
    Long countByEntityTypeAndAction(@Param("entityType") AuditEntityType entityType, 
                                   @Param("action") AuditAction action);
    
    // Count by user and action
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.userId = :userId AND al.action = :action AND al.active = true")
    Long countByUserIdAndAction(@Param("userId") Long userId, @Param("action") AuditAction action);
    
    // Find recent audit logs for compliance scoring
    @Query("SELECT al FROM AuditLog al WHERE al.entityType IN (:entityTypes) AND al.createdAt BETWEEN :startDate AND :endDate AND al.active = true ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentAuditLogsForCompliance(@Param("entityTypes") List<AuditEntityType> entityTypes,
                                                   @Param("startDate") Instant startDate,
                                                   @Param("endDate") Instant endDate);
}

