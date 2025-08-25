package org.driver.driverapp.service;

import org.driver.driverapp.dto.product.request.CreateProductRequestDTO;
import org.driver.driverapp.dto.product.request.UpdateProductRequestDTO;
import org.driver.driverapp.dto.product.response.ProductResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.ProductMapper;
import org.driver.driverapp.model.Product;
import org.driver.driverapp.model.Supplier;
import org.driver.driverapp.repository.ProductRepository;
import org.driver.driverapp.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Supplier testSupplier;
    private CreateProductRequestDTO createRequestDTO;
    private UpdateProductRequestDTO updateRequestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testSupplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .phone("+251911234567")
                .email("supplier@test.com")
                .address("Test Address")
                .active(true)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Test Category")
                .sku("TEST-001")
                .price(new BigDecimal("100.00"))
                .unit("kg")
                .supplier(testSupplier)
                .description("Test Description")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        createRequestDTO = CreateProductRequestDTO.builder()
                .name("Test Product")
                .category("Test Category")
                .sku("TEST-001")
                .price(new BigDecimal("100.00"))
                .unit("kg")
                .supplierId(1L)
                .description("Test Description")
                .build();

        updateRequestDTO = UpdateProductRequestDTO.builder()
                .name("Updated Product")
                .price(new BigDecimal("150.00"))
                .description("Updated Description")
                .build();

        responseDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Test Product")
                .category("Test Category")
                .sku("TEST-001")
                .price(new BigDecimal("100.00"))
                .unit("kg")
                .supplierId(1L)
                .supplierName("Test Supplier")
                .supplierPhone("+251911234567")
                .supplierEmail("supplier@test.com")
                .description("Test Description")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .formattedPrice("ETB 100.00")
                .hasSupplier(true)
                .build();
    }

    @Test
    void createProduct_Success() {
        // Given
        when(productRepository.existsBySku("TEST-001")).thenReturn(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(productMapper.toEntity(createRequestDTO)).thenReturn(testProduct);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.createProduct(createRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("TEST-001", result.getSku());
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        verify(productRepository).existsBySku("TEST-001");
        verify(supplierRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_SkuAlreadyExists_ThrowsException() {
        // Given
        when(productRepository.existsBySku("TEST-001")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(createRequestDTO));
        verify(productRepository).existsBySku("TEST-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_SupplierNotFound_ThrowsException() {
        // Given
        when(productRepository.existsBySku("TEST-001")).thenReturn(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());
        when(productMapper.toEntity(createRequestDTO)).thenReturn(testProduct);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(createRequestDTO));
        verify(productRepository).existsBySku("TEST-001");
        verify(supplierRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductBySku_Success() {
        // Given
        when(productRepository.findBySkuAndActiveTrue("TEST-001")).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.getProductBySku("TEST-001");

        // Then
        assertNotNull(result);
        assertEquals("TEST-001", result.getSku());
        verify(productRepository).findBySkuAndActiveTrue("TEST-001");
    }

    @Test
    void getProductBySku_NotFound_ThrowsException() {
        // Given
        when(productRepository.findBySkuAndActiveTrue("TEST-001")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductBySku("TEST-001"));
        verify(productRepository).findBySkuAndActiveTrue("TEST-001");
    }

    @Test
    void updateProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.updateProduct(1L, updateRequestDTO);

        // Then
        assertNotNull(result);
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntityFromDto(updateRequestDTO, testProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, updateRequestDTO));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_SkuAlreadyExists_ThrowsException() {
        // Given
        UpdateProductRequestDTO updateWithSku = UpdateProductRequestDTO.builder()
                .sku("NEW-SKU")
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsBySkuAndIdNot("NEW-SKU", 1L)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, updateWithSku));
        verify(productRepository).findById(1L);
        verify(productRepository).existsBySkuAndIdNot("NEW-SKU", 1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
        assertFalse(testProduct.isActive());
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getAllActiveProducts_WithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByActiveTrue(pageable)).thenReturn(productPage);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        Page<ProductResponseDTO> result = productService.getAllActiveProducts(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findByActiveTrue(pageable);
    }

    @Test
    void getAllActiveProducts_WithoutPagination_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.getAllActiveProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void getProductsByCategory_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryIgnoreCaseAndActiveTrue("Test Category")).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.getProductsByCategory("Test Category");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findByCategoryIgnoreCaseAndActiveTrue("Test Category");
    }

    @Test
    void getProductsBySupplier_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findBySupplierIdAndActiveTrue(1L)).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.getProductsBySupplier(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findBySupplierIdAndActiveTrue(1L);
    }

    @Test
    void searchProducts_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByNameOrDescription("test")).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.searchProducts("test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).searchByNameOrDescription("test");
    }

    @Test
    void getProductsByPriceRange_Success() {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice)).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    @Test
    void getProductsByCategories_Success() {
        // Given
        List<String> categories = Arrays.asList("Test Category", "Another Category");
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryInAndActiveTrue(categories)).thenReturn(products);
        when(productMapper.toResponseDTOList(products)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<ProductResponseDTO> result = productService.getProductsByCategories(categories);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findByCategoryInAndActiveTrue(categories);
    }

    @Test
    void getAllCategories_Success() {
        // Given
        List<String> categories = Arrays.asList("Test Category", "Another Category");
        when(productRepository.findAllCategories()).thenReturn(categories);

        // When
        List<String> result = productService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findAllCategories();
    }

    @Test
    void getProductStatistics_Success() {
        // Given
        when(productRepository.countActiveProducts()).thenReturn(10L);
        when(productRepository.countProductsWithSupplier()).thenReturn(8L);
        when(productRepository.countProductsWithoutSupplier()).thenReturn(2L);
        when(productRepository.findAllCategories()).thenReturn(Arrays.asList("Category1", "Category2"));

        // When
        ProductService.ProductStatistics result = productService.getProductStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalProducts());
        assertEquals(8L, result.getProductsWithSupplier());
        assertEquals(2L, result.getProductsWithoutSupplier());
        assertEquals(2, result.getCategories().size());
    }

    @Test
    void activateProduct_Success() {
        // Given
        testProduct.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.activateProduct(1L);

        // Then
        assertNotNull(result);
        assertTrue(testProduct.isActive());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void deactivateProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(responseDTO);

        // When
        ProductResponseDTO result = productService.deactivateProduct(1L);

        // Then
        assertNotNull(result);
        assertFalse(testProduct.isActive());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void productExists_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = productService.productExists(1L);

        // Then
        assertTrue(result);
        verify(productRepository).existsById(1L);
    }

    @Test
    void isProductActive_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = productService.isProductActive(1L);

        // Then
        assertTrue(result);
        verify(productRepository).findById(1L);
    }

    @Test
    void isProductActive_ProductNotFound_ReturnsFalse() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = productService.isProductActive(1L);

        // Then
        assertFalse(result);
        verify(productRepository).findById(1L);
    }
}
