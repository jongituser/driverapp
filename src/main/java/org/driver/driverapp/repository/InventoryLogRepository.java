package org.driver.driverapp.repository;

import org.driver.driverapp.model.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    // Find by inventory item
    List<InventoryLog> findByInventoryItemId(Long inventoryItemId);
    
    Page<InventoryLog> findByInventoryItemId(Long inventoryItemId, Pageable pageable);

    // Find by partner (location)
    List<InventoryLog> findByPartnerId(Long partnerId);
    
    Page<InventoryLog> findByPartnerId(Long partnerId, Pageable pageable);

    // Find by user (who made the change)
    List<InventoryLog> findByUserId(Long userId);
    
    Page<InventoryLog> findByUserId(Long userId, Pageable pageable);

    // Find by log type
    List<InventoryLog> findByLogType(InventoryLog.LogType logType);
    
    Page<InventoryLog> findByLogType(InventoryLog.LogType logType, Pageable pageable);

    // Find by partner and log type
    List<InventoryLog> findByPartnerIdAndLogType(Long partnerId, InventoryLog.LogType logType);
    
    Page<InventoryLog> findByPartnerIdAndLogType(Long partnerId, InventoryLog.LogType logType, Pageable pageable);

    // Find by inventory item and log type
    List<InventoryLog> findByInventoryItemIdAndLogType(Long inventoryItemId, InventoryLog.LogType logType);
    
    Page<InventoryLog> findByInventoryItemIdAndLogType(Long inventoryItemId, InventoryLog.LogType logType, Pageable pageable);

    // Find by date range
    @Query("SELECT l FROM InventoryLog l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryLog> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    Page<InventoryLog> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, Pageable pageable);

    // Find by partner and date range
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryLog> findByPartnerIdAndCreatedAtBetween(@Param("partnerId") Long partnerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.createdAt BETWEEN :startDate AND :endDate")
    Page<InventoryLog> findByPartnerIdAndCreatedAtBetween(@Param("partnerId") Long partnerId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate, Pageable pageable);

    // Find by inventory item and date range
    @Query("SELECT l FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.createdAt BETWEEN :startDate AND :endDate")
    List<InventoryLog> findByInventoryItemIdAndCreatedAtBetween(@Param("inventoryItemId") Long inventoryItemId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.createdAt BETWEEN :startDate AND :endDate")
    Page<InventoryLog> findByInventoryItemIdAndCreatedAtBetween(@Param("inventoryItemId") Long inventoryItemId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate, Pageable pageable);

    // Find stock movements (in/out) by partner
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType IN ('STOCK_IN', 'STOCK_OUT') ORDER BY l.createdAt DESC")
    List<InventoryLog> findStockMovementsByPartner(@Param("partnerId") Long partnerId);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType IN ('STOCK_IN', 'STOCK_OUT') ORDER BY l.createdAt DESC")
    Page<InventoryLog> findStockMovementsByPartner(@Param("partnerId") Long partnerId, Pageable pageable);

    // Find stock movements by inventory item
    @Query("SELECT l FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.logType IN ('STOCK_IN', 'STOCK_OUT') ORDER BY l.createdAt DESC")
    List<InventoryLog> findStockMovementsByInventoryItem(@Param("inventoryItemId") Long inventoryItemId);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.logType IN ('STOCK_IN', 'STOCK_OUT') ORDER BY l.createdAt DESC")
    Page<InventoryLog> findStockMovementsByInventoryItem(@Param("inventoryItemId") Long inventoryItemId, Pageable pageable);

    // Find adjustments by partner
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'STOCK_ADJUSTMENT' ORDER BY l.createdAt DESC")
    List<InventoryLog> findAdjustmentsByPartner(@Param("partnerId") Long partnerId);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'STOCK_ADJUSTMENT' ORDER BY l.createdAt DESC")
    Page<InventoryLog> findAdjustmentsByPartner(@Param("partnerId") Long partnerId, Pageable pageable);

    // Find expiry write-offs by partner
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'EXPIRY_WRITE_OFF' ORDER BY l.createdAt DESC")
    List<InventoryLog> findExpiryWriteOffsByPartner(@Param("partnerId") Long partnerId);
    
    @Query("SELECT l FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'EXPIRY_WRITE_OFF' ORDER BY l.createdAt DESC")
    Page<InventoryLog> findExpiryWriteOffsByPartner(@Param("partnerId") Long partnerId, Pageable pageable);

    // Count logs by partner
    long countByPartnerId(Long partnerId);

    // Count logs by inventory item
    long countByInventoryItemId(Long inventoryItemId);

    // Count logs by log type
    long countByLogType(InventoryLog.LogType logType);

    // Count logs by partner and log type
    long countByPartnerIdAndLogType(Long partnerId, InventoryLog.LogType logType);

    // Get total stock in by partner
    @Query("SELECT SUM(l.quantityChanged) FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'STOCK_IN'")
    Integer getTotalStockInByPartner(@Param("partnerId") Long partnerId);

    // Get total stock out by partner
    @Query("SELECT SUM(l.quantityChanged) FROM InventoryLog l WHERE l.partner.id = :partnerId AND l.logType = 'STOCK_OUT'")
    Integer getTotalStockOutByPartner(@Param("partnerId") Long partnerId);

    // Get total stock in by inventory item
    @Query("SELECT SUM(l.quantityChanged) FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.logType = 'STOCK_IN'")
    Integer getTotalStockInByInventoryItem(@Param("inventoryItemId") Long inventoryItemId);

    // Get total stock out by inventory item
    @Query("SELECT SUM(l.quantityChanged) FROM InventoryLog l WHERE l.inventoryItem.id = :inventoryItemId AND l.logType = 'STOCK_OUT'")
    Integer getTotalStockOutByInventoryItem(@Param("inventoryItemId") Long inventoryItemId);
}
