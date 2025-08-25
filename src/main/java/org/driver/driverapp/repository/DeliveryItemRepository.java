package org.driver.driverapp.repository;

import org.driver.driverapp.model.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {

    // Find by delivery
    List<DeliveryItem> findByDeliveryId(Long deliveryId);

    // Find by delivery and active status
    List<DeliveryItem> findByDeliveryIdAndActiveTrue(Long deliveryId);

    // Find by product
    List<DeliveryItem> findByProductId(Long productId);

    // Find by product and active status
    List<DeliveryItem> findByProductIdAndActiveTrue(Long productId);

    // Find by delivery and product
    Optional<DeliveryItem> findByDeliveryIdAndProductId(Long deliveryId, Long productId);

    // Find by delivery and product and active status
    Optional<DeliveryItem> findByDeliveryIdAndProductIdAndActiveTrue(Long deliveryId, Long productId);

    // Find active delivery items
    List<DeliveryItem> findByActiveTrue();

    // Count by delivery
    long countByDeliveryId(Long deliveryId);

    // Count by delivery and active status
    long countByDeliveryIdAndActiveTrue(Long deliveryId);

    // Count by product
    long countByProductId(Long productId);

    // Count by product and active status
    long countByProductIdAndActiveTrue(Long productId);

    // Check if delivery item exists for delivery and product
    boolean existsByDeliveryIdAndProductId(Long deliveryId, Long productId);

    // Check if delivery item exists for delivery and product and active
    boolean existsByDeliveryIdAndProductIdAndActiveTrue(Long deliveryId, Long productId);

    // Find delivery items by product category
    @Query("SELECT di FROM DeliveryItem di WHERE di.active = true AND di.product.category = :category")
    List<DeliveryItem> findByProductCategory(@Param("category") String category);

    // Find delivery items by product supplier
    @Query("SELECT di FROM DeliveryItem di WHERE di.active = true AND di.product.supplier.id = :supplierId")
    List<DeliveryItem> findByProductSupplierId(@Param("supplierId") Long supplierId);

    // Get delivery items statistics
    @Query("SELECT COUNT(di) FROM DeliveryItem di WHERE di.active = true")
    long countActiveDeliveryItems();

    @Query("SELECT COUNT(di) FROM DeliveryItem di WHERE di.active = true AND di.delivery.id = :deliveryId")
    long countActiveDeliveryItemsByDelivery(@Param("deliveryId") Long deliveryId);

    @Query("SELECT SUM(di.total) FROM DeliveryItem di WHERE di.active = true AND di.delivery.id = :deliveryId")
    java.math.BigDecimal getTotalAmountByDelivery(@Param("deliveryId") Long deliveryId);

    @Query("SELECT SUM(di.quantity) FROM DeliveryItem di WHERE di.active = true AND di.delivery.id = :deliveryId")
    Integer getTotalQuantityByDelivery(@Param("deliveryId") Long deliveryId);

    // Find delivery items with total amount greater than threshold
    @Query("SELECT di FROM DeliveryItem di WHERE di.active = true AND di.total > :threshold")
    List<DeliveryItem> findByTotalGreaterThan(@Param("threshold") java.math.BigDecimal threshold);

    // Find delivery items by price range
    @Query("SELECT di FROM DeliveryItem di WHERE di.active = true AND di.price BETWEEN :minPrice AND :maxPrice")
    List<DeliveryItem> findByPriceBetween(@Param("minPrice") java.math.BigDecimal minPrice, 
                                         @Param("maxPrice") java.math.BigDecimal maxPrice);
}
