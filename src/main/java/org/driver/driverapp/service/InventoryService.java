package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final PartnerRepository partnerRepository;
    private final InventoryItemMapper inventoryItemMapper;

    // Create inventory item
    public InventoryItemResponseDTO createInventoryItem(CreateInventoryItemRequestDTO request) {
        log.info("Creating inventory item: {}", request.getName());

        // Validate partner exists
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + request.getPartnerId()));

        // Check if SKU already exists
        if (inventoryItemRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }

        // Create inventory item
        InventoryItem inventoryItem = inventoryItemMapper.toEntity(request);
        inventoryItem = inventoryItemRepository.save(inventoryItem);

        // Create initial stock log
        createInventoryLog(inventoryItem, 0, inventoryItem.getQuantity(), 
                inventoryItem.getQuantity(), InventoryLog.LogType.INITIAL_STOCK, 
                "Initial stock", null, partner);

        log.info("Created inventory item with id: {}", inventoryItem.getId());
        return inventoryItemMapper.toResponseDto(inventoryItem);
    }

    // Update inventory item
    public InventoryItemResponseDTO updateInventoryItem(Long id, UpdateInventoryItemRequestDTO request) {
        log.info("Updating inventory item with id: {}", id);

        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));

        // Store old quantity for logging
        int oldQuantity = inventoryItem.getQuantity();

        // Update the entity
        inventoryItemMapper.updateEntityFromDto(request, inventoryItem);
        inventoryItem = inventoryItemRepository.save(inventoryItem);

        // Log quantity changes if quantity was updated
        if (request.getQuantity() != null && !request.getQuantity().equals(oldQuantity)) {
            int quantityChange = request.getQuantity() - oldQuantity;
            String reason = quantityChange > 0 ? "Stock adjustment - increase" : "Stock adjustment - decrease";
            
            createInventoryLog(inventoryItem, oldQuantity, request.getQuantity(), 
                    Math.abs(quantityChange), InventoryLog.LogType.STOCK_ADJUSTMENT, 
                    reason, request.getNotes(), inventoryItem.getPartner());
        }

        log.info("Updated inventory item with id: {}", id);
        return inventoryItemMapper.toResponseDto(inventoryItem);
    }

    // Delete inventory item
    public void deleteInventoryItem(Long id) {
        log.info("Deleting inventory item with id: {}", id);

        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));

        inventoryItem.setActive(false);
        inventoryItemRepository.save(inventoryItem);

        log.info("Deleted inventory item with id: {}", id);
    }

    // Get inventory item by id
    public InventoryItemResponseDTO getInventoryItemById(Long id) {
        log.info("Getting inventory item with id: {}", id);

        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));

        return inventoryItemMapper.toResponseDto(inventoryItem);
    }

    // Get all inventory items with pagination
    public Page<InventoryItemResponseDTO> getAllInventoryItems(Pageable pageable) {
        log.info("Getting all inventory items with pagination");

        Page<InventoryItem> inventoryItems = inventoryItemRepository.findByActiveTrue(pageable);
        return inventoryItems.map(inventoryItemMapper::toResponseDto);
    }

    // Get inventory items by partner
    public Page<InventoryItemResponseDTO> getInventoryItemsByPartner(Long partnerId, Pageable pageable) {
        log.info("Getting inventory items for partner: {}", partnerId);

        Page<InventoryItem> inventoryItems = inventoryItemRepository.findByPartnerIdAndActiveTrue(partnerId, pageable);
        return inventoryItems.map(inventoryItemMapper::toResponseDto);
    }

    // Get inventory items by category
    public Page<InventoryItemResponseDTO> getInventoryItemsByCategory(String category, Pageable pageable) {
        log.info("Getting inventory items for category: {}", category);

        Page<InventoryItem> inventoryItems = inventoryItemRepository.findByCategory(category, pageable);
        return inventoryItems.map(inventoryItemMapper::toResponseDto);
    }

    // Search inventory items by name
    public Page<InventoryItemResponseDTO> searchInventoryItemsByName(String searchTerm, Pageable pageable) {
        log.info("Searching inventory items by name: {}", searchTerm);

        Page<InventoryItem> inventoryItems = inventoryItemRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        return inventoryItems.map(inventoryItemMapper::toResponseDto);
    }

    // Get low stock items
    public List<InventoryItemResponseDTO> getLowStockItems() {
        log.info("Getting low stock items");

        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItems();
        return lowStockItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Get low stock items by partner
    public List<InventoryItemResponseDTO> getLowStockItemsByPartner(Long partnerId) {
        log.info("Getting low stock items for partner: {}", partnerId);

        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItemsByPartner(partnerId);
        return lowStockItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Get expired items
    public List<InventoryItemResponseDTO> getExpiredItems() {
        log.info("Getting expired items");

        List<InventoryItem> expiredItems = inventoryItemRepository.findExpiredItems(LocalDate.now());
        return expiredItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Get expired items by partner
    public List<InventoryItemResponseDTO> getExpiredItemsByPartner(Long partnerId) {
        log.info("Getting expired items for partner: {}", partnerId);

        List<InventoryItem> expiredItems = inventoryItemRepository.findExpiredItemsByPartner(partnerId, LocalDate.now());
        return expiredItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Get items expiring soon
    public List<InventoryItemResponseDTO> getItemsExpiringSoon(int daysThreshold) {
        log.info("Getting items expiring within {} days", daysThreshold);

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(daysThreshold);
        
        List<InventoryItem> expiringItems = inventoryItemRepository.findItemsExpiringSoon(today, expiryDate);
        return expiringItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Get items expiring soon by partner
    public List<InventoryItemResponseDTO> getItemsExpiringSoonByPartner(Long partnerId, int daysThreshold) {
        log.info("Getting items expiring within {} days for partner: {}", daysThreshold, partnerId);

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(daysThreshold);
        
        List<InventoryItem> expiringItems = inventoryItemRepository.findItemsExpiringSoonByPartner(partnerId, today, expiryDate);
        return expiringItems.stream()
                .map(inventoryItemMapper::toResponseDto)
                .toList();
    }

    // Stock adjustment
    public InventoryItemResponseDTO adjustStock(StockAdjustmentRequestDTO request) {
        log.info("Adjusting stock for inventory item: {}", request.getInventoryItemId());

        InventoryItem inventoryItem = inventoryItemRepository.findById(request.getInventoryItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + request.getInventoryItemId()));

        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + request.getPartnerId()));

        int oldQuantity = inventoryItem.getQuantity();
        int newQuantity = oldQuantity + request.getQuantityChange();

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below 0. Current: " + oldQuantity + ", Requested reduction: " + Math.abs(request.getQuantityChange()));
        }

        // Update stock level
        inventoryItem.updateStockLevel(newQuantity);
        inventoryItem = inventoryItemRepository.save(inventoryItem);

        // Determine log type
        InventoryLog.LogType logType = request.getQuantityChange() > 0 ? 
                InventoryLog.LogType.STOCK_IN : InventoryLog.LogType.STOCK_OUT;

        // Create inventory log
        createInventoryLog(inventoryItem, oldQuantity, newQuantity, 
                Math.abs(request.getQuantityChange()), logType, 
                request.getReason(), request.getNotes(), partner);

        log.info("Adjusted stock for inventory item: {}. Old: {}, New: {}", 
                request.getInventoryItemId(), oldQuantity, newQuantity);

        return inventoryItemMapper.toResponseDto(inventoryItem);
    }

    // Add stock
    public InventoryItemResponseDTO addStock(Long inventoryItemId, int quantity, String reason, String notes, Long partnerId) {
        log.info("Adding stock for inventory item: {}. Quantity: {}", inventoryItemId, quantity);

        StockAdjustmentRequestDTO request = StockAdjustmentRequestDTO.builder()
                .inventoryItemId(inventoryItemId)
                .quantityChange(quantity)
                .reason(reason != null ? reason : "Stock addition")
                .notes(notes)
                .partnerId(partnerId)
                .build();

        return adjustStock(request);
    }

    // Remove stock
    public InventoryItemResponseDTO removeStock(Long inventoryItemId, int quantity, String reason, String notes, Long partnerId) {
        log.info("Removing stock for inventory item: {}. Quantity: {}", inventoryItemId, quantity);

        StockAdjustmentRequestDTO request = StockAdjustmentRequestDTO.builder()
                .inventoryItemId(inventoryItemId)
                .quantityChange(-quantity)
                .reason(reason != null ? reason : "Stock removal")
                .notes(notes)
                .partnerId(partnerId)
                .build();

        return adjustStock(request);
    }

    // Write off expired items
    public void writeOffExpiredItems(Long partnerId) {
        log.info("Writing off expired items for partner: {}", partnerId);

        List<InventoryItem> expiredItems = inventoryItemRepository.findExpiredItemsByPartner(partnerId, LocalDate.now());
        
        for (InventoryItem item : expiredItems) {
            if (item.getQuantity() > 0) {
                int oldQuantity = item.getQuantity();
                
                // Set quantity to 0
                item.updateStockLevel(0);
                inventoryItemRepository.save(item);

                // Create expiry write-off log
                createInventoryLog(item, oldQuantity, 0, oldQuantity, 
                        InventoryLog.LogType.EXPIRY_WRITE_OFF, 
                        "Expired item write-off", "Automatic write-off of expired items", 
                        item.getPartner());

                log.info("Wrote off expired item: {}. Quantity: {}", item.getName(), oldQuantity);
            }
        }
    }

    // Get inventory statistics
    public InventoryStatistics getInventoryStatistics(Long partnerId) {
        log.info("Getting inventory statistics for partner: {}", partnerId);

        long totalItems = inventoryItemRepository.countByPartnerId(partnerId);
        long lowStockItems = inventoryItemRepository.countLowStockItemsByPartner(partnerId);
        long expiredItems = inventoryItemRepository.countExpiredItemsByPartner(partnerId, LocalDate.now());
        BigDecimal totalValue = inventoryItemRepository.getTotalInventoryValueByPartner(partnerId);

        return InventoryStatistics.builder()
                .totalItems(totalItems)
                .lowStockItems(lowStockItems)
                .expiredItems(expiredItems)
                .totalValue(totalValue != null ? totalValue : BigDecimal.ZERO)
                .build();
    }

    // Create inventory log
    private void createInventoryLog(InventoryItem inventoryItem, int quantityBefore, int quantityAfter, 
                                   int quantityChanged, InventoryLog.LogType logType, String reason, 
                                   String notes, Partner partner) {
        
        InventoryLog inventoryLog = InventoryLog.builder()
                .inventoryItem(inventoryItem)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .quantityChanged(quantityChanged)
                .logType(logType)
                .reason(reason)
                .notes(notes)
                .partner(partner)
                .unitPrice(inventoryItem.getUnitPrice())
                .totalValue(inventoryItem.getTotalValue())
                .build();

        inventoryLogRepository.save(inventoryLog);
        log.info("Created inventory log: {} - {} ({} -> {})", 
                inventoryItem.getName(), logType.name(), quantityBefore, quantityAfter);
    }

    // Statistics DTO
    public static class InventoryStatistics {
        private long totalItems;
        private long lowStockItems;
        private long expiredItems;
        private BigDecimal totalValue;

        // Builder pattern
        public static InventoryStatisticsBuilder builder() {
            return new InventoryStatisticsBuilder();
        }

        public static class InventoryStatisticsBuilder {
            private long totalItems;
            private long lowStockItems;
            private long expiredItems;
            private BigDecimal totalValue;

            public InventoryStatisticsBuilder totalItems(long totalItems) {
                this.totalItems = totalItems;
                return this;
            }

            public InventoryStatisticsBuilder lowStockItems(long lowStockItems) {
                this.lowStockItems = lowStockItems;
                return this;
            }

            public InventoryStatisticsBuilder expiredItems(long expiredItems) {
                this.expiredItems = expiredItems;
                return this;
            }

            public InventoryStatisticsBuilder totalValue(BigDecimal totalValue) {
                this.totalValue = totalValue;
                return this;
            }

            public InventoryStatistics build() {
                InventoryStatistics statistics = new InventoryStatistics();
                statistics.totalItems = this.totalItems;
                statistics.lowStockItems = this.lowStockItems;
                statistics.expiredItems = this.expiredItems;
                statistics.totalValue = this.totalValue;
                return statistics;
            }
        }

        // Getters
        public long getTotalItems() { return totalItems; }
        public long getLowStockItems() { return lowStockItems; }
        public long getExpiredItems() { return expiredItems; }
        public BigDecimal getTotalValue() { return totalValue; }
    }
}
