package org.driver.driverapp.repository;

import org.driver.driverapp.model.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    // Find by partner (location)
    List<InventoryItem> findByPartnerId(Long partnerId);
    
    Page<InventoryItem> findByPartnerId(Long partnerId, Pageable pageable);

    // Find by supplier
    List<InventoryItem> findBySupplierId(Long supplierId);
    
    Page<InventoryItem> findBySupplierId(Long supplierId, Pageable pageable);

    // Find by partner and supplier
    List<InventoryItem> findByPartnerIdAndSupplierId(Long partnerId, Long supplierId);
    
    Page<InventoryItem> findByPartnerIdAndSupplierId(Long partnerId, Long supplierId, Pageable pageable);

    // Find active items
    List<InventoryItem> findByActiveTrue();
    
    Page<InventoryItem> findByActiveTrue(Pageable pageable);

    // Find by partner and active status
    List<InventoryItem> findByPartnerIdAndActiveTrue(Long partnerId);
    
    Page<InventoryItem> findByPartnerIdAndActiveTrue(Long partnerId, Pageable pageable);

    // Find by category
    List<InventoryItem> findByCategory(String category);
    
    Page<InventoryItem> findByCategory(String category, Pageable pageable);

    // Find by partner and category
    List<InventoryItem> findByPartnerIdAndCategory(Long partnerId, String category);
    
    Page<InventoryItem> findByPartnerIdAndCategory(Long partnerId, String category, Pageable pageable);

    // Find by SKU
    Optional<InventoryItem> findBySku(String sku);

    // Find by batch number
    List<InventoryItem> findByBatchNumber(String batchNumber);
    
    Page<InventoryItem> findByBatchNumber(String batchNumber, Pageable pageable);

    // Find by partner and batch number
    List<InventoryItem> findByPartnerIdAndBatchNumber(Long partnerId, String batchNumber);

    // Find low stock items (quantity <= minimumStockThreshold)
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minimumStockThreshold AND i.active = true")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minimumStockThreshold AND i.active = true")
    Page<InventoryItem> findLowStockItems(Pageable pageable);

    // Find low stock items by partner
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.quantity <= i.minimumStockThreshold AND i.active = true")
    List<InventoryItem> findLowStockItemsByPartner(@Param("partnerId") Long partnerId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.quantity <= i.minimumStockThreshold AND i.active = true")
    Page<InventoryItem> findLowStockItemsByPartner(@Param("partnerId") Long partnerId, Pageable pageable);

    // Find expired items
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate < :today AND i.active = true")
    List<InventoryItem> findExpiredItems(@Param("today") LocalDate today);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate < :today AND i.active = true")
    Page<InventoryItem> findExpiredItems(@Param("today") LocalDate today, Pageable pageable);

    // Find expired items by partner
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate < :today AND i.active = true")
    List<InventoryItem> findExpiredItemsByPartner(@Param("partnerId") Long partnerId, @Param("today") LocalDate today);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate < :today AND i.active = true")
    Page<InventoryItem> findExpiredItemsByPartner(@Param("partnerId") Long partnerId, @Param("today") LocalDate today, Pageable pageable);

    // Find items expiring soon (within specified days)
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate BETWEEN :today AND :expiryDate AND i.active = true")
    List<InventoryItem> findItemsExpiringSoon(@Param("today") LocalDate today, @Param("expiryDate") LocalDate expiryDate);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate BETWEEN :today AND :expiryDate AND i.active = true")
    Page<InventoryItem> findItemsExpiringSoon(@Param("today") LocalDate today, @Param("expiryDate") LocalDate expiryDate, Pageable pageable);

    // Find items expiring soon by partner
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate BETWEEN :today AND :expiryDate AND i.active = true")
    List<InventoryItem> findItemsExpiringSoonByPartner(@Param("partnerId") Long partnerId, @Param("today") LocalDate today, @Param("expiryDate") LocalDate expiryDate);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate BETWEEN :today AND :expiryDate AND i.active = true")
    Page<InventoryItem> findItemsExpiringSoonByPartner(@Param("partnerId") Long partnerId, @Param("today") LocalDate today, @Param("expiryDate") LocalDate expiryDate, Pageable pageable);

    // Search items by name containing
    @Query("SELECT i FROM InventoryItem i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND i.active = true")
    Page<InventoryItem> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Search items by name containing and partner
    @Query("SELECT i FROM InventoryItem i WHERE i.partner.id = :partnerId AND LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND i.active = true")
    Page<InventoryItem> findByNameContainingIgnoreCaseAndPartner(@Param("partnerId") Long partnerId, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Count items by partner
    long countByPartnerId(Long partnerId);

    // Count active items
    long countByActiveTrue();

    // Count low stock items
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.quantity <= i.minimumStockThreshold AND i.active = true")
    long countLowStockItems();

    // Count low stock items by partner
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.quantity <= i.minimumStockThreshold AND i.active = true")
    long countLowStockItemsByPartner(@Param("partnerId") Long partnerId);

    // Count expired items
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.expiryDate < :today AND i.active = true")
    long countExpiredItems(@Param("today") LocalDate today);

    // Count expired items by partner
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate < :today AND i.active = true")
    long countExpiredItemsByPartner(@Param("partnerId") Long partnerId, @Param("today") LocalDate today);

    // Check if item exists by SKU
    boolean existsBySku(String sku);

    // Check if item exists by SKU and partner
    boolean existsBySkuAndPartnerId(String sku, Long partnerId);

    // Get total inventory value by partner
    @Query("SELECT SUM(i.totalValue) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.active = true")
    java.math.BigDecimal getTotalInventoryValueByPartner(@Param("partnerId") Long partnerId);

    // Get total inventory value
    @Query("SELECT SUM(i.totalValue) FROM InventoryItem i WHERE i.active = true")
    java.math.BigDecimal getTotalInventoryValue();
    
    // Analytics methods
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.active = true")
    Long countByPartnerIdAndActiveTrue(@Param("partnerId") Long partnerId);
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.quantity < :quantity AND i.active = true")
    Long countByPartnerIdAndQuantityLessThanAndActiveTrue(@Param("partnerId") Long partnerId, @Param("quantity") Long quantity);
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.quantity = :quantity AND i.active = true")
    Long countByPartnerIdAndQuantityEqualsAndActiveTrue(@Param("partnerId") Long partnerId, @Param("quantity") Long quantity);
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.expiryDate < :expiryDate AND i.active = true")
    Long countByPartnerIdAndExpiryDateBeforeAndActiveTrue(@Param("partnerId") Long partnerId, @Param("expiryDate") LocalDate expiryDate);
    
    @Query("SELECT SUM(i.totalValue) FROM InventoryItem i WHERE i.partner.id = :partnerId AND i.active = true")
    java.math.BigDecimal sumTotalValueByPartnerIdAndActiveTrue(@Param("partnerId") Long partnerId);
}
