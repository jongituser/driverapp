package org.driver.driverapp.service;

import org.driver.driverapp.dto.inventory.request.CreateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.request.StockAdjustmentRequestDTO;
import org.driver.driverapp.dto.inventory.request.UpdateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.response.InventoryItemResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.InventoryItemMapper;
import org.driver.driverapp.model.InventoryItem;
import org.driver.driverapp.model.InventoryLog;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.InventoryItemRepository;
import org.driver.driverapp.repository.InventoryLogRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.repository.UserRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryItemMapper inventoryItemMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private Partner testPartner;
    private InventoryItem testInventoryItem;
    private CreateInventoryItemRequestDTO createRequest;
    private UpdateInventoryItemRequestDTO updateRequest;
    private StockAdjustmentRequestDTO stockAdjustmentRequest;

    @BeforeEach
    void setUp() {
        // Setup test partner
        testPartner = Partner.builder()
                .id(1L)
                .name("Test Partner")
                .phone("+251911234567")
                .email("test@partner.com")
                .active(true)
                .build();

        // Setup test inventory item
        testInventoryItem = InventoryItem.builder()
                .id(1L)
                .name("Test Item")
                .category("Test Category")
                .sku("TEST-001")
                .quantity(100)
                .unit("pieces")
                .minimumStockThreshold(10)
                .unitPrice(BigDecimal.valueOf(5.00))
                .totalValue(BigDecimal.valueOf(500.00))
                .batchNumber("BATCH-001")
                .expiryDate(LocalDate.now().plusDays(30))
                .description("Test description")
                .active(true)
                .lowStockAlert(false)
                .expired(false)
                .partner(testPartner)
                .build();

        // Setup create request
        createRequest = CreateInventoryItemRequestDTO.builder()
                .name("Test Item")
                .category("Test Category")
                .sku("TEST-001")
                .quantity(100)
                .unit("pieces")
                .minimumStockThreshold(10)
                .unitPrice(BigDecimal.valueOf(5.00))
                .batchNumber("BATCH-001")
                .expiryDate(LocalDate.now().plusDays(30))
                .description("Test description")
                .partnerId(1L)
                .build();

        // Setup update request
        updateRequest = UpdateInventoryItemRequestDTO.builder()
                .name("Updated Item")
                .quantity(150)
                .unitPrice(BigDecimal.valueOf(6.00))
                .build();

        // Setup stock adjustment request
        stockAdjustmentRequest = StockAdjustmentRequestDTO.builder()
                .inventoryItemId(1L)
                .quantityChange(50)
                .reason("Stock addition")
                .notes("Test stock addition")
                .partnerId(1L)
                .build();
    }

    @Test
    void createInventoryItem_Success() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.existsBySku("TEST-001")).thenReturn(false);
        when(inventoryItemMapper.toEntity(createRequest)).thenReturn(testInventoryItem);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.createInventoryItem(createRequest);

        // Then
        assertNotNull(result);
        verify(partnerRepository).findById(1L);
        verify(inventoryItemRepository).existsBySku("TEST-001");
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void createInventoryItem_PartnerNotFound() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.createInventoryItem(createRequest);
        });
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void createInventoryItem_SkuAlreadyExists() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.existsBySku("TEST-001")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.createInventoryItem(createRequest);
        });
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void updateInventoryItem_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.updateInventoryItem(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_ItemNotFound() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.updateInventoryItem(1L, updateRequest);
        });
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void deleteInventoryItem_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);

        // When
        inventoryService.deleteInventoryItem(1L);

        // Then
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        assertFalse(testInventoryItem.isActive());
    }

    @Test
    void getInventoryItemById_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.getInventoryItemById(1L);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
    }

    @Test
    void getInventoryItemById_NotFound() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.getInventoryItemById(1L);
        });
    }

    @Test
    void getAllInventoryItems_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<InventoryItem> page = new PageImpl<>(List.of(testInventoryItem));
        when(inventoryItemRepository.findByActiveTrue(pageable)).thenReturn(page);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        Page<InventoryItemResponseDTO> result = inventoryService.getAllInventoryItems(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(inventoryItemRepository).findByActiveTrue(pageable);
    }

    @Test
    void getInventoryItemsByPartner_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<InventoryItem> page = new PageImpl<>(List.of(testInventoryItem));
        when(inventoryItemRepository.findByPartnerIdAndActiveTrue(1L, pageable)).thenReturn(page);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        Page<InventoryItemResponseDTO> result = inventoryService.getInventoryItemsByPartner(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(inventoryItemRepository).findByPartnerIdAndActiveTrue(1L, pageable);
    }

    @Test
    void adjustStock_AddStock_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.adjustStock(stockAdjustmentRequest);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void adjustStock_RemoveStock_Success() {
        // Given
        stockAdjustmentRequest.setQuantityChange(-20);
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.adjustStock(stockAdjustmentRequest);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void adjustStock_InsufficientStock() {
        // Given
        stockAdjustmentRequest.setQuantityChange(-150); // More than available (100)
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.adjustStock(stockAdjustmentRequest);
        });
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void getLowStockItems_Success() {
        // Given
        List<InventoryItem> lowStockItems = List.of(testInventoryItem);
        when(inventoryItemRepository.findLowStockItems()).thenReturn(lowStockItems);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        List<InventoryItemResponseDTO> result = inventoryService.getLowStockItems();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryItemRepository).findLowStockItems();
    }

    @Test
    void getExpiredItems_Success() {
        // Given
        List<InventoryItem> expiredItems = List.of(testInventoryItem);
        when(inventoryItemRepository.findExpiredItems(LocalDate.now())).thenReturn(expiredItems);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        List<InventoryItemResponseDTO> result = inventoryService.getExpiredItems();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryItemRepository).findExpiredItems(LocalDate.now());
    }

    @Test
    void getItemsExpiringSoon_Success() {
        // Given
        List<InventoryItem> expiringItems = List.of(testInventoryItem);
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(30);
        when(inventoryItemRepository.findItemsExpiringSoon(today, expiryDate)).thenReturn(expiringItems);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        List<InventoryItemResponseDTO> result = inventoryService.getItemsExpiringSoon(30);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inventoryItemRepository).findItemsExpiringSoon(today, expiryDate);
    }

    @Test
    void writeOffExpiredItems_Success() {
        // Given
        InventoryItem expiredItem = InventoryItem.builder()
                .id(1L)
                .name("Test Item")
                .category("Test Category")
                .sku("TEST-001")
                .quantity(50)
                .unit("pieces")
                .minimumStockThreshold(10)
                .unitPrice(BigDecimal.valueOf(5.00))
                .totalValue(BigDecimal.valueOf(250.00))
                .batchNumber("BATCH-001")
                .expiryDate(LocalDate.now().minusDays(1))
                .description("Test description")
                .active(true)
                .lowStockAlert(false)
                .expired(true)
                .partner(testPartner)
                .build();
        List<InventoryItem> expiredItems = List.of(expiredItem);
        when(inventoryItemRepository.findExpiredItemsByPartner(1L, LocalDate.now())).thenReturn(expiredItems);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(expiredItem);

        // When
        inventoryService.writeOffExpiredItems(1L);

        // Then
        verify(inventoryItemRepository).findExpiredItemsByPartner(1L, LocalDate.now());
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void getInventoryStatistics_Success() {
        // Given
        when(inventoryItemRepository.countByPartnerId(1L)).thenReturn(10L);
        when(inventoryItemRepository.countLowStockItemsByPartner(1L)).thenReturn(2L);
        when(inventoryItemRepository.countExpiredItemsByPartner(1L, LocalDate.now())).thenReturn(1L);
        when(inventoryItemRepository.getTotalInventoryValueByPartner(1L)).thenReturn(BigDecimal.valueOf(1000.00));

        // When
        InventoryService.InventoryStatistics result = inventoryService.getInventoryStatistics(1L);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalItems());
        assertEquals(2L, result.getLowStockItems());
        assertEquals(1L, result.getExpiredItems());
        assertEquals(BigDecimal.valueOf(1000.00), result.getTotalValue());
    }

    @Test
    void addStock_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.addStock(1L, 50, "Test addition", "Test notes", 1L);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void removeStock_Success() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);
        when(inventoryItemMapper.toResponseDto(testInventoryItem)).thenReturn(new InventoryItemResponseDTO());

        // When
        InventoryItemResponseDTO result = inventoryService.removeStock(1L, 20, "Test removal", "Test notes", 1L);

        // Then
        assertNotNull(result);
        verify(inventoryItemRepository).findById(1L);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
        verify(inventoryLogRepository).save(any(InventoryLog.class));
    }

    @Test
    void removeStock_InsufficientStock() {
        // Given
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(testInventoryItem));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.removeStock(1L, 150, "Test removal", "Test notes", 1L);
        });
        verify(inventoryItemRepository, never()).save(any());
    }
}
