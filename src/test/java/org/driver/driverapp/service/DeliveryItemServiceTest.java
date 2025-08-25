package org.driver.driverapp.service;

import org.driver.driverapp.dto.delivery.request.CreateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.request.UpdateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryItemResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.DeliveryItemMapper;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.DeliveryItem;
import org.driver.driverapp.model.Product;
import org.driver.driverapp.repository.DeliveryItemRepository;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryItemServiceTest {

    @Mock
    private DeliveryItemRepository deliveryItemRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DeliveryItemMapper deliveryItemMapper;

    @InjectMocks
    private DeliveryItemService deliveryItemService;

    private Delivery testDelivery;
    private Product testProduct;
    private DeliveryItem testDeliveryItem;
    private CreateDeliveryItemRequestDTO createRequestDTO;
    private UpdateDeliveryItemRequestDTO updateRequestDTO;
    private DeliveryItemResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testDelivery = Delivery.builder()
                .id(1L)
                .deliveryCode("DEL-001")
                .status("PENDING")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Test Category")
                .sku("TEST-001")
                .price(new BigDecimal("100.00"))
                .unit("kg")
                .active(true)
                .build();

        testDeliveryItem = DeliveryItem.builder()
                .id(1L)
                .delivery(testDelivery)
                .product(testProduct)
                .quantity(5)
                .price(new BigDecimal("100.00"))
                .total(new BigDecimal("500.00"))
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        createRequestDTO = CreateDeliveryItemRequestDTO.builder()
                .deliveryId(1L)
                .productId(1L)
                .quantity(5)
                .price(new BigDecimal("100.00"))
                .build();

        updateRequestDTO = UpdateDeliveryItemRequestDTO.builder()
                .quantity(10)
                .price(new BigDecimal("120.00"))
                .build();

        responseDTO = DeliveryItemResponseDTO.builder()
                .id(1L)
                .deliveryId(1L)
                .productId(1L)
                .quantity(5)
                .price(new BigDecimal("100.00"))
                .total(new BigDecimal("500.00"))
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .productName("Test Product")
                .productCategory("Test Category")
                .productSku("TEST-001")
                .productUnit("kg")
                .productDescription("Test Description")
                .formattedPrice("ETB 100.00")
                .formattedTotal("ETB 500.00")
                .hasProduct(true)
                .build();
    }

    @Test
    void createDeliveryItem_Success() {
        // Given
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(deliveryItemRepository.existsByDeliveryIdAndProductIdAndActiveTrue(1L, 1L)).thenReturn(false);
        when(deliveryItemMapper.toEntity(createRequestDTO)).thenReturn(testDeliveryItem);
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.createDeliveryItem(createRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getDeliveryId());
        assertEquals(1L, result.getProductId());
        assertEquals(5, result.getQuantity());
        verify(deliveryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(deliveryItemRepository).existsByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository).save(any(DeliveryItem.class));
    }

    @Test
    void createDeliveryItem_DeliveryNotFound_ThrowsException() {
        // Given
        when(deliveryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.createDeliveryItem(createRequestDTO));
        verify(deliveryRepository).findById(1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void createDeliveryItem_ProductNotFound_ThrowsException() {
        // Given
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.createDeliveryItem(createRequestDTO));
        verify(deliveryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void createDeliveryItem_InactiveProduct_ThrowsException() {
        // Given
        testProduct.setActive(false);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> deliveryItemService.createDeliveryItem(createRequestDTO));
        verify(deliveryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void createDeliveryItem_DeliveryItemAlreadyExists_ThrowsException() {
        // Given
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(deliveryItemRepository.existsByDeliveryIdAndProductIdAndActiveTrue(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> deliveryItemService.createDeliveryItem(createRequestDTO));
        verify(deliveryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(deliveryItemRepository).existsByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void getDeliveryItemById_Success() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.getDeliveryItemById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(deliveryItemRepository).findById(1L);
    }

    @Test
    void getDeliveryItemById_NotFound_ThrowsException() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.getDeliveryItemById(1L));
        verify(deliveryItemRepository).findById(1L);
    }

    @Test
    void updateDeliveryItem_Success() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.updateDeliveryItem(1L, updateRequestDTO);

        // Then
        assertNotNull(result);
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemMapper).updateEntityFromDto(updateRequestDTO, testDeliveryItem);
        verify(deliveryItemRepository).save(testDeliveryItem);
    }

    @Test
    void updateDeliveryItem_NotFound_ThrowsException() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.updateDeliveryItem(1L, updateRequestDTO));
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void deleteDeliveryItem_Success() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);

        // When
        deliveryItemService.deleteDeliveryItem(1L);

        // Then
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemRepository).save(testDeliveryItem);
        assertFalse(testDeliveryItem.isActive());
    }

    @Test
    void deleteDeliveryItem_NotFound_ThrowsException() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.deleteDeliveryItem(1L));
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void getDeliveryItemsByDelivery_Success() {
        // Given
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByDeliveryIdAndActiveTrue(1L)).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByDelivery(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByDeliveryIdAndActiveTrue(1L);
    }

    @Test
    void getDeliveryItemsByProduct_Success() {
        // Given
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByProductIdAndActiveTrue(1L)).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByProduct(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByProductIdAndActiveTrue(1L);
    }

    @Test
    void getDeliveryItemsByProductCategory_Success() {
        // Given
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByProductCategory("Test Category")).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByProductCategory("Test Category");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByProductCategory("Test Category");
    }

    @Test
    void getDeliveryItemsByProductSupplier_Success() {
        // Given
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByProductSupplierId(1L)).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByProductSupplier(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByProductSupplierId(1L);
    }

    @Test
    void getAllActiveDeliveryItems_Success() {
        // Given
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByActiveTrue()).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getAllActiveDeliveryItems();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByActiveTrue();
    }

    @Test
    void addProductToDelivery_Success() {
        // Given
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(deliveryItemRepository.existsByDeliveryIdAndProductIdAndActiveTrue(1L, 1L)).thenReturn(false);
        when(deliveryItemMapper.toEntity(any(CreateDeliveryItemRequestDTO.class))).thenReturn(testDeliveryItem);
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.addProductToDelivery(1L, 1L, 5, new BigDecimal("100.00"));

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getDeliveryId());
        assertEquals(1L, result.getProductId());
        verify(deliveryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(deliveryItemRepository).save(any(DeliveryItem.class));
    }

    @Test
    void removeProductFromDelivery_Success() {
        // Given
        when(deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L))
                .thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.findById(testDeliveryItem.getId())).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);

        // When
        deliveryItemService.removeProductFromDelivery(1L, 1L);

        // Then
        verify(deliveryItemRepository).findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository).findById(testDeliveryItem.getId());
        verify(deliveryItemRepository).save(testDeliveryItem);
        assertFalse(testDeliveryItem.isActive());
    }

    @Test
    void removeProductFromDelivery_NotFound_ThrowsException() {
        // Given
        when(deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.removeProductFromDelivery(1L, 1L));
        verify(deliveryItemRepository).findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void updateProductQuantity_Success() {
        // Given
        when(deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L))
                .thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.findById(testDeliveryItem.getId())).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.updateProductQuantity(1L, 1L, 10);

        // Then
        assertNotNull(result);
        verify(deliveryItemRepository).findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository).findById(testDeliveryItem.getId());
        verify(deliveryItemRepository).save(testDeliveryItem);
    }

    @Test
    void updateProductQuantity_NotFound_ThrowsException() {
        // Given
        when(deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deliveryItemService.updateProductQuantity(1L, 1L, 10));
        verify(deliveryItemRepository).findByDeliveryIdAndProductIdAndActiveTrue(1L, 1L);
        verify(deliveryItemRepository, never()).save(any(DeliveryItem.class));
    }

    @Test
    void getDeliveryTotalAmount_Success() {
        // Given
        when(deliveryItemRepository.getTotalAmountByDelivery(1L)).thenReturn(new BigDecimal("500.00"));

        // When
        BigDecimal result = deliveryItemService.getDeliveryTotalAmount(1L);

        // Then
        assertEquals(new BigDecimal("500.00"), result);
        verify(deliveryItemRepository).getTotalAmountByDelivery(1L);
    }

    @Test
    void getDeliveryTotalAmount_ReturnsZero_WhenNull() {
        // Given
        when(deliveryItemRepository.getTotalAmountByDelivery(1L)).thenReturn(null);

        // When
        BigDecimal result = deliveryItemService.getDeliveryTotalAmount(1L);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(deliveryItemRepository).getTotalAmountByDelivery(1L);
    }

    @Test
    void getDeliveryTotalQuantity_Success() {
        // Given
        when(deliveryItemRepository.getTotalQuantityByDelivery(1L)).thenReturn(15);

        // When
        Integer result = deliveryItemService.getDeliveryTotalQuantity(1L);

        // Then
        assertEquals(15, result);
        verify(deliveryItemRepository).getTotalQuantityByDelivery(1L);
    }

    @Test
    void getDeliveryTotalQuantity_ReturnsZero_WhenNull() {
        // Given
        when(deliveryItemRepository.getTotalQuantityByDelivery(1L)).thenReturn(null);

        // When
        Integer result = deliveryItemService.getDeliveryTotalQuantity(1L);

        // Then
        assertEquals(0, result);
        verify(deliveryItemRepository).getTotalQuantityByDelivery(1L);
    }

    @Test
    void getDeliveryItemsByTotalThreshold_Success() {
        // Given
        BigDecimal threshold = new BigDecimal("100.00");
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByTotalGreaterThan(threshold)).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByTotalThreshold(threshold);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByTotalGreaterThan(threshold);
    }

    @Test
    void getDeliveryItemsByPriceRange_Success() {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        List<DeliveryItem> deliveryItems = Arrays.asList(testDeliveryItem);
        when(deliveryItemRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(deliveryItems);
        when(deliveryItemMapper.toResponseDTOList(deliveryItems)).thenReturn(Arrays.asList(responseDTO));

        // When
        List<DeliveryItemResponseDTO> result = deliveryItemService.getDeliveryItemsByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(deliveryItemRepository).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    void activateDeliveryItem_Success() {
        // Given
        testDeliveryItem.setActive(false);
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.activateDeliveryItem(1L);

        // Then
        assertNotNull(result);
        assertTrue(testDeliveryItem.isActive());
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemRepository).save(testDeliveryItem);
    }

    @Test
    void deactivateDeliveryItem_Success() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));
        when(deliveryItemRepository.save(any(DeliveryItem.class))).thenReturn(testDeliveryItem);
        when(deliveryItemMapper.toResponseDTO(testDeliveryItem)).thenReturn(responseDTO);

        // When
        DeliveryItemResponseDTO result = deliveryItemService.deactivateDeliveryItem(1L);

        // Then
        assertNotNull(result);
        assertFalse(testDeliveryItem.isActive());
        verify(deliveryItemRepository).findById(1L);
        verify(deliveryItemRepository).save(testDeliveryItem);
    }

    @Test
    void deliveryItemExists_Success() {
        // Given
        when(deliveryItemRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = deliveryItemService.deliveryItemExists(1L);

        // Then
        assertTrue(result);
        verify(deliveryItemRepository).existsById(1L);
    }

    @Test
    void isDeliveryItemActive_Success() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.of(testDeliveryItem));

        // When
        boolean result = deliveryItemService.isDeliveryItemActive(1L);

        // Then
        assertTrue(result);
        verify(deliveryItemRepository).findById(1L);
    }

    @Test
    void isDeliveryItemActive_NotFound_ReturnsFalse() {
        // Given
        when(deliveryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = deliveryItemService.isDeliveryItemActive(1L);

        // Then
        assertFalse(result);
        verify(deliveryItemRepository).findById(1L);
    }

    @Test
    void getDeliveryItemStatistics_Success() {
        // Given
        when(deliveryItemRepository.countActiveDeliveryItems()).thenReturn(25L);

        // When
        DeliveryItemService.DeliveryItemStatistics result = deliveryItemService.getDeliveryItemStatistics();

        // Then
        assertNotNull(result);
        assertEquals(25L, result.getTotalDeliveryItems());
        verify(deliveryItemRepository).countActiveDeliveryItems();
    }
}
