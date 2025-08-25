package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.inventory.request.CreateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.request.StockAdjustmentRequestDTO;
import org.driver.driverapp.dto.inventory.request.UpdateInventoryItemRequestDTO;
import org.driver.driverapp.dto.inventory.response.InventoryItemResponseDTO;
import org.driver.driverapp.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // Create inventory item
    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryItemResponseDTO> createInventoryItem(
            @Valid @RequestBody CreateInventoryItemRequestDTO request) {
        log.info("Creating inventory item: {}", request.getName());
        InventoryItemResponseDTO response = inventoryService.createInventoryItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get inventory item by id
    @GetMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<InventoryItemResponseDTO> getInventoryItemById(@PathVariable Long id) {
        log.info("Getting inventory item with id: {}", id);
        InventoryItemResponseDTO response = inventoryService.getInventoryItemById(id);
        return ResponseEntity.ok(response);
    }

    // Get all inventory items with pagination
    @GetMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<InventoryItemResponseDTO>> getAllInventoryItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all inventory items with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryItemResponseDTO> response = inventoryService.getAllInventoryItems(pageable);
        return ResponseEntity.ok(response);
    }

    // Get inventory items by partner
    @GetMapping("/partners/{partnerId}/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<InventoryItemResponseDTO>> getInventoryItemsByPartner(
            @PathVariable Long partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting inventory items for partner: {}", partnerId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryItemResponseDTO> response = inventoryService.getInventoryItemsByPartner(partnerId, pageable);
        return ResponseEntity.ok(response);
    }

    // Get inventory items by category
    @GetMapping("/items/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<InventoryItemResponseDTO>> getInventoryItemsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting inventory items for category: {}", category);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryItemResponseDTO> response = inventoryService.getInventoryItemsByCategory(category, pageable);
        return ResponseEntity.ok(response);
    }

    // Search inventory items by name
    @GetMapping("/items/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<InventoryItemResponseDTO>> searchInventoryItemsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Searching inventory items by name: {}", name);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InventoryItemResponseDTO> response = inventoryService.searchInventoryItemsByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    // Update inventory item
    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryItemResponseDTO> updateInventoryItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInventoryItemRequestDTO request) {
        log.info("Updating inventory item with id: {}", id);
        InventoryItemResponseDTO response = inventoryService.updateInventoryItem(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete inventory item
    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        log.info("Deleting inventory item with id: {}", id);
        inventoryService.deleteInventoryItem(id);
        return ResponseEntity.noContent().build();
    }

    // Stock adjustment
    @PostMapping("/items/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryItemResponseDTO> adjustStock(
            @Valid @RequestBody StockAdjustmentRequestDTO request) {
        log.info("Adjusting stock for inventory item: {}", request.getInventoryItemId());
        InventoryItemResponseDTO response = inventoryService.adjustStock(request);
        return ResponseEntity.ok(response);
    }

    // Add stock
    @PostMapping("/items/{id}/add-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryItemResponseDTO> addStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String notes,
            @RequestParam Long partnerId) {
        
        log.info("Adding stock for inventory item: {}. Quantity: {}", id, quantity);
        InventoryItemResponseDTO response = inventoryService.addStock(id, quantity, reason, notes, partnerId);
        return ResponseEntity.ok(response);
    }

    // Remove stock
    @PostMapping("/items/{id}/remove-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryItemResponseDTO> removeStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String notes,
            @RequestParam Long partnerId) {
        
        log.info("Removing stock for inventory item: {}. Quantity: {}", id, quantity);
        InventoryItemResponseDTO response = inventoryService.removeStock(id, quantity, reason, notes, partnerId);
        return ResponseEntity.ok(response);
    }

    // Get low stock items
    @GetMapping("/items/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getLowStockItems() {
        log.info("Getting low stock items");
        List<InventoryItemResponseDTO> response = inventoryService.getLowStockItems();
        return ResponseEntity.ok(response);
    }

    // Get low stock items by partner
    @GetMapping("/partners/{partnerId}/items/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getLowStockItemsByPartner(@PathVariable Long partnerId) {
        log.info("Getting low stock items for partner: {}", partnerId);
        List<InventoryItemResponseDTO> response = inventoryService.getLowStockItemsByPartner(partnerId);
        return ResponseEntity.ok(response);
    }

    // Get expired items
    @GetMapping("/items/expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getExpiredItems() {
        log.info("Getting expired items");
        List<InventoryItemResponseDTO> response = inventoryService.getExpiredItems();
        return ResponseEntity.ok(response);
    }

    // Get expired items by partner
    @GetMapping("/partners/{partnerId}/items/expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getExpiredItemsByPartner(@PathVariable Long partnerId) {
        log.info("Getting expired items for partner: {}", partnerId);
        List<InventoryItemResponseDTO> response = inventoryService.getExpiredItemsByPartner(partnerId);
        return ResponseEntity.ok(response);
    }

    // Get items expiring soon
    @GetMapping("/items/expiring-soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getItemsExpiringSoon(
            @RequestParam(defaultValue = "30") int daysThreshold) {
        log.info("Getting items expiring within {} days", daysThreshold);
        List<InventoryItemResponseDTO> response = inventoryService.getItemsExpiringSoon(daysThreshold);
        return ResponseEntity.ok(response);
    }

    // Get items expiring soon by partner
    @GetMapping("/partners/{partnerId}/items/expiring-soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<InventoryItemResponseDTO>> getItemsExpiringSoonByPartner(
            @PathVariable Long partnerId,
            @RequestParam(defaultValue = "30") int daysThreshold) {
        log.info("Getting items expiring within {} days for partner: {}", daysThreshold, partnerId);
        List<InventoryItemResponseDTO> response = inventoryService.getItemsExpiringSoonByPartner(partnerId, daysThreshold);
        return ResponseEntity.ok(response);
    }

    // Write off expired items
    @PostMapping("/partners/{partnerId}/write-off-expired")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> writeOffExpiredItems(@PathVariable Long partnerId) {
        log.info("Writing off expired items for partner: {}", partnerId);
        inventoryService.writeOffExpiredItems(partnerId);
        return ResponseEntity.ok().build();
    }

    // Get inventory statistics
    @GetMapping("/partners/{partnerId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<InventoryService.InventoryStatistics> getInventoryStatistics(@PathVariable Long partnerId) {
        log.info("Getting inventory statistics for partner: {}", partnerId);
        InventoryService.InventoryStatistics response = inventoryService.getInventoryStatistics(partnerId);
        return ResponseEntity.ok(response);
    }
}
