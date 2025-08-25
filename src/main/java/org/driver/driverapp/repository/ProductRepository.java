package org.driver.driverapp.repository;

import org.driver.driverapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by SKU (unique)
    Optional<Product> findBySku(String sku);

    // Find by SKU and active status
    Optional<Product> findBySkuAndActiveTrue(String sku);

    // Find by category
    List<Product> findByCategoryIgnoreCase(String category);

    // Find by category and active status
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);

    // Find by supplier
    List<Product> findBySupplierId(Long supplierId);

    // Find by supplier and active status
    List<Product> findBySupplierIdAndActiveTrue(Long supplierId);

    // Find active products
    List<Product> findByActiveTrue();

    // Find active products with pagination
    Page<Product> findByActiveTrue(Pageable pageable);

    // Search by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Search by name and active status
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Search by name or description (case-insensitive)
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    // Find by price range
    List<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    // Find by multiple categories
    List<Product> findByCategoryInAndActiveTrue(List<String> categories);

    // Count by category
    long countByCategory(String category);

    // Count by supplier
    long countBySupplierId(Long supplierId);

    // Check if SKU exists
    boolean existsBySku(String sku);

    // Check if SKU exists (excluding current product)
    boolean existsBySkuAndIdNot(String sku, Long id);

    // Find products with no supplier
    List<Product> findBySupplierIsNullAndActiveTrue();

    // Find products by supplier name
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.supplier.name LIKE %:supplierName%")
    List<Product> findBySupplierNameContaining(@Param("supplierName") String supplierName);

    // Get all categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findAllCategories();

    // Get product statistics
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.supplier IS NOT NULL")
    long countProductsWithSupplier();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.supplier IS NULL")
    long countProductsWithoutSupplier();
}
