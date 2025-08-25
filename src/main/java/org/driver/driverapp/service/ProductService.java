package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.product.request.CreateProductRequestDTO;
import org.driver.driverapp.dto.product.request.UpdateProductRequestDTO;
import org.driver.driverapp.dto.product.response.ProductResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.ProductMapper;
import org.driver.driverapp.model.Product;
import org.driver.driverapp.model.Supplier;
import org.driver.driverapp.repository.ProductRepository;
import org.driver.driverapp.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    // Create product
    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        log.info("Creating product: {}", requestDTO.getName());

        // Check if SKU already exists
        if (productRepository.existsBySku(requestDTO.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + requestDTO.getSku() + " already exists");
        }

        // Create product entity
        Product product = productMapper.toEntity(requestDTO);
        product.setActive(true);

        // Set supplier if provided
        if (requestDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + requestDTO.getSupplierId()));
            product.setSupplier(supplier);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Created product with id: {}", savedProduct.getId());

        return productMapper.toResponseDTO(savedProduct);
    }

    // Get product by ID
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        log.info("Getting product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    // Get product by SKU
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductBySku(String sku) {
        log.info("Getting product by SKU: {}", sku);
        Product product = productRepository.findBySkuAndActiveTrue(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return productMapper.toResponseDTO(product);
    }

    // Update product
    public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO requestDTO) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Check if SKU is being changed and if it already exists
        if (requestDTO.getSku() != null && !requestDTO.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndIdNot(requestDTO.getSku(), id)) {
                throw new IllegalArgumentException("Product with SKU " + requestDTO.getSku() + " already exists");
            }
        }

        // Update supplier if provided
        if (requestDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + requestDTO.getSupplierId()));
            product.setSupplier(supplier);
        }

        productMapper.updateEntityFromDto(requestDTO, product);
        Product updatedProduct = productRepository.save(product);
        log.info("Updated product with id: {}", updatedProduct.getId());

        return productMapper.toResponseDTO(updatedProduct);
    }

    // Delete product (soft delete)
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.deactivate();
        productRepository.save(product);
        log.info("Deleted product with id: {}", id);
    }

    // Get all active products with pagination
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllActiveProducts(Pageable pageable) {
        log.info("Getting all active products with pagination");
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return products.map(productMapper::toResponseDTO);
    }

    // Get all active products
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllActiveProducts() {
        log.info("Getting all active products");
        List<Product> products = productRepository.findByActiveTrue();
        return productMapper.toResponseDTOList(products);
    }

    // Get products by category
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        log.info("Getting products by category: {}", category);
        List<Product> products = productRepository.findByCategoryIgnoreCaseAndActiveTrue(category);
        return productMapper.toResponseDTOList(products);
    }

    // Get products by supplier
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsBySupplier(Long supplierId) {
        log.info("Getting products by supplier: {}", supplierId);
        List<Product> products = productRepository.findBySupplierIdAndActiveTrue(supplierId);
        return productMapper.toResponseDTOList(products);
    }

    // Search products by name or description
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String searchTerm) {
        log.info("Searching products with term: {}", searchTerm);
        List<Product> products = productRepository.searchByNameOrDescription(searchTerm);
        return productMapper.toResponseDTOList(products);
    }

    // Get products by price range
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Getting products by price range: {} - {}", minPrice, maxPrice);
        List<Product> products = productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
        return productMapper.toResponseDTOList(products);
    }

    // Get products by multiple categories
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategories(List<String> categories) {
        log.info("Getting products by categories: {}", categories);
        List<Product> products = productRepository.findByCategoryInAndActiveTrue(categories);
        return productMapper.toResponseDTOList(products);
    }

    // Get all categories
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.info("Getting all product categories");
        return productRepository.findAllCategories();
    }

    // Get product statistics
    @Transactional(readOnly = true)
    public ProductStatistics getProductStatistics() {
        log.info("Getting product statistics");
        long totalProducts = productRepository.countActiveProducts();
        long productsWithSupplier = productRepository.countProductsWithSupplier();
        long productsWithoutSupplier = productRepository.countProductsWithoutSupplier();
        List<String> categories = productRepository.findAllCategories();

        return ProductStatistics.builder()
                .totalProducts(totalProducts)
                .productsWithSupplier(productsWithSupplier)
                .productsWithoutSupplier(productsWithoutSupplier)
                .categories(categories)
                .build();
    }

    // Activate product
    public ProductResponseDTO activateProduct(Long id) {
        log.info("Activating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.activate();
        Product activatedProduct = productRepository.save(product);
        log.info("Activated product with id: {}", activatedProduct.getId());
        return productMapper.toResponseDTO(activatedProduct);
    }

    // Deactivate product
    public ProductResponseDTO deactivateProduct(Long id) {
        log.info("Deactivating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.deactivate();
        Product deactivatedProduct = productRepository.save(product);
        log.info("Deactivated product with id: {}", deactivatedProduct.getId());
        return productMapper.toResponseDTO(deactivatedProduct);
    }

    // Check if product exists
    @Transactional(readOnly = true)
    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }

    // Check if product is active
    @Transactional(readOnly = true)
    public boolean isProductActive(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(Product::isActive).orElse(false);
    }

    // Inner class for statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProductStatistics {
        private long totalProducts;
        private long productsWithSupplier;
        private long productsWithoutSupplier;
        private List<String> categories;
    }
}
