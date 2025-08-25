package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.product.request.CreateProductRequestDTO;
import org.driver.driverapp.dto.product.request.UpdateProductRequestDTO;
import org.driver.driverapp.dto.product.response.ProductResponseDTO;
import org.driver.driverapp.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // Create product
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO requestDTO) {
        log.info("Creating product: {}", requestDTO.getName());
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get product by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        log.info("Getting product by id: {}", id);
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // Get product by SKU
    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<ProductResponseDTO> getProductBySku(@PathVariable String sku) {
        log.info("Getting product by SKU: {}", sku);
        ProductResponseDTO response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    // Update product
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, 
                                                          @Valid @RequestBody UpdateProductRequestDTO requestDTO) {
        log.info("Updating product with id: {}", id);
        ProductResponseDTO response = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // Delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Get all active products with pagination
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Page<ProductResponseDTO>> getAllActiveProducts(Pageable pageable) {
        log.info("Getting all active products with pagination");
        Page<ProductResponseDTO> response = productService.getAllActiveProducts(pageable);
        return ResponseEntity.ok(response);
    }

    // Get all active products (no pagination)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<ProductResponseDTO>> getAllActiveProducts() {
        log.info("Getting all active products");
        List<ProductResponseDTO> response = productService.getAllActiveProducts();
        return ResponseEntity.ok(response);
    }

    // Get products by category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable String category) {
        log.info("Getting products by category: {}", category);
        List<ProductResponseDTO> response = productService.getProductsByCategory(category);
        return ResponseEntity.ok(response);
    }

    // Get products by supplier
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<ProductResponseDTO>> getProductsBySupplier(@PathVariable Long supplierId) {
        log.info("Getting products by supplier: {}", supplierId);
        List<ProductResponseDTO> response = productService.getProductsBySupplier(supplierId);
        return ResponseEntity.ok(response);
    }

    // Search products
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String searchTerm) {
        log.info("Searching products with term: {}", searchTerm);
        List<ProductResponseDTO> response = productService.searchProducts(searchTerm);
        return ResponseEntity.ok(response);
    }

    // Get products by price range
    @GetMapping("/price-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        log.info("Getting products by price range: {} - {}", minPrice, maxPrice);
        List<ProductResponseDTO> response = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    // Get products by multiple categories
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategories(@RequestBody List<String> categories) {
        log.info("Getting products by categories: {}", categories);
        List<ProductResponseDTO> response = productService.getProductsByCategories(categories);
        return ResponseEntity.ok(response);
    }

    // Get all categories
    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("Getting all product categories");
        List<String> response = productService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    // Get product statistics
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<ProductService.ProductStatistics> getProductStatistics() {
        log.info("Getting product statistics");
        ProductService.ProductStatistics response = productService.getProductStatistics();
        return ResponseEntity.ok(response);
    }

    // Activate product
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<ProductResponseDTO> activateProduct(@PathVariable Long id) {
        log.info("Activating product with id: {}", id);
        ProductResponseDTO response = productService.activateProduct(id);
        return ResponseEntity.ok(response);
    }

    // Deactivate product
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<ProductResponseDTO> deactivateProduct(@PathVariable Long id) {
        log.info("Deactivating product with id: {}", id);
        ProductResponseDTO response = productService.deactivateProduct(id);
        return ResponseEntity.ok(response);
    }

    // Check if product exists
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Boolean> productExists(@PathVariable Long id) {
        log.info("Checking if product exists with id: {}", id);
        boolean exists = productService.productExists(id);
        return ResponseEntity.ok(exists);
    }

    // Check if product is active
    @GetMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Boolean> isProductActive(@PathVariable Long id) {
        log.info("Checking if product is active with id: {}", id);
        boolean active = productService.isProductActive(id);
        return ResponseEntity.ok(active);
    }
}
