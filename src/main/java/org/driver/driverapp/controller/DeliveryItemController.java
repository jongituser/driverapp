package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.delivery.request.CreateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.request.UpdateDeliveryItemRequestDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryItemResponseDTO;
import org.driver.driverapp.service.DeliveryItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery-items")
@RequiredArgsConstructor
@Slf4j
public class DeliveryItemController {

    private final DeliveryItemService deliveryItemService;

    // Create delivery item
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> createDeliveryItem(@Valid @RequestBody CreateDeliveryItemRequestDTO requestDTO) {
        log.info("Creating delivery item for delivery: {} and product: {}", 
                requestDTO.getDeliveryId(), requestDTO.getProductId());
        DeliveryItemResponseDTO response = deliveryItemService.createDeliveryItem(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get delivery item by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<DeliveryItemResponseDTO> getDeliveryItemById(@PathVariable Long id) {
        log.info("Getting delivery item by id: {}", id);
        DeliveryItemResponseDTO response = deliveryItemService.getDeliveryItemById(id);
        return ResponseEntity.ok(response);
    }

    // Update delivery item
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> updateDeliveryItem(@PathVariable Long id, 
                                                                    @Valid @RequestBody UpdateDeliveryItemRequestDTO requestDTO) {
        log.info("Updating delivery item with id: {}", id);
        DeliveryItemResponseDTO response = deliveryItemService.updateDeliveryItem(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    // Delete delivery item
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<Void> deleteDeliveryItem(@PathVariable Long id) {
        log.info("Deleting delivery item with id: {}", id);
        deliveryItemService.deleteDeliveryItem(id);
        return ResponseEntity.noContent().build();
    }

    // Get delivery items by delivery
    @GetMapping("/delivery/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByDelivery(@PathVariable Long deliveryId) {
        log.info("Getting delivery items for delivery: {}", deliveryId);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByDelivery(deliveryId);
        return ResponseEntity.ok(response);
    }

    // Get delivery items by product
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByProduct(@PathVariable Long productId) {
        log.info("Getting delivery items for product: {}", productId);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByProduct(productId);
        return ResponseEntity.ok(response);
    }

    // Get delivery items by product category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByProductCategory(@PathVariable String category) {
        log.info("Getting delivery items for product category: {}", category);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByProductCategory(category);
        return ResponseEntity.ok(response);
    }

    // Get delivery items by product supplier
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByProductSupplier(@PathVariable Long supplierId) {
        log.info("Getting delivery items for product supplier: {}", supplierId);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByProductSupplier(supplierId);
        return ResponseEntity.ok(response);
    }

    // Get all active delivery items
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getAllActiveDeliveryItems() {
        log.info("Getting all active delivery items");
        List<DeliveryItemResponseDTO> response = deliveryItemService.getAllActiveDeliveryItems();
        return ResponseEntity.ok(response);
    }

    // Add product to delivery (convenience endpoint)
    @PostMapping("/delivery/{deliveryId}/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> addProductToDelivery(@PathVariable Long deliveryId,
                                                                      @PathVariable Long productId,
                                                                      @RequestParam Integer quantity,
                                                                      @RequestParam BigDecimal price) {
        log.info("Adding product {} to delivery {} with quantity {} and price {}", 
                productId, deliveryId, quantity, price);
        DeliveryItemResponseDTO response = deliveryItemService.addProductToDelivery(deliveryId, productId, quantity, price);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Remove product from delivery
    @DeleteMapping("/delivery/{deliveryId}/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<Void> removeProductFromDelivery(@PathVariable Long deliveryId, @PathVariable Long productId) {
        log.info("Removing product {} from delivery {}", productId, deliveryId);
        deliveryItemService.removeProductFromDelivery(deliveryId, productId);
        return ResponseEntity.noContent().build();
    }

    // Update quantity of product in delivery
    @PatchMapping("/delivery/{deliveryId}/product/{productId}/quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> updateProductQuantity(@PathVariable Long deliveryId,
                                                                       @PathVariable Long productId,
                                                                       @RequestParam Integer newQuantity) {
        log.info("Updating quantity of product {} in delivery {} to {}", productId, deliveryId, newQuantity);
        DeliveryItemResponseDTO response = deliveryItemService.updateProductQuantity(deliveryId, productId, newQuantity);
        return ResponseEntity.ok(response);
    }

    // Get delivery total amount
    @GetMapping("/delivery/{deliveryId}/total-amount")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<BigDecimal> getDeliveryTotalAmount(@PathVariable Long deliveryId) {
        log.info("Getting total amount for delivery: {}", deliveryId);
        BigDecimal total = deliveryItemService.getDeliveryTotalAmount(deliveryId);
        return ResponseEntity.ok(total);
    }

    // Get delivery total quantity
    @GetMapping("/delivery/{deliveryId}/total-quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Integer> getDeliveryTotalQuantity(@PathVariable Long deliveryId) {
        log.info("Getting total quantity for delivery: {}", deliveryId);
        Integer total = deliveryItemService.getDeliveryTotalQuantity(deliveryId);
        return ResponseEntity.ok(total);
    }

    // Get delivery items with total amount greater than threshold
    @GetMapping("/total-threshold")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByTotalThreshold(@RequestParam BigDecimal threshold) {
        log.info("Getting delivery items with total amount greater than: {}", threshold);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByTotalThreshold(threshold);
        return ResponseEntity.ok(response);
    }

    // Get delivery items by price range
    @GetMapping("/price-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<List<DeliveryItemResponseDTO>> getDeliveryItemsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        log.info("Getting delivery items by price range: {} - {}", minPrice, maxPrice);
        List<DeliveryItemResponseDTO> response = deliveryItemService.getDeliveryItemsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    // Activate delivery item
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> activateDeliveryItem(@PathVariable Long id) {
        log.info("Activating delivery item with id: {}", id);
        DeliveryItemResponseDTO response = deliveryItemService.activateDeliveryItem(id);
        return ResponseEntity.ok(response);
    }

    // Deactivate delivery item
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemResponseDTO> deactivateDeliveryItem(@PathVariable Long id) {
        log.info("Deactivating delivery item with id: {}", id);
        DeliveryItemResponseDTO response = deliveryItemService.deactivateDeliveryItem(id);
        return ResponseEntity.ok(response);
    }

    // Check if delivery item exists
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Boolean> deliveryItemExists(@PathVariable Long id) {
        log.info("Checking if delivery item exists with id: {}", id);
        boolean exists = deliveryItemService.deliveryItemExists(id);
        return ResponseEntity.ok(exists);
    }

    // Check if delivery item is active
    @GetMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER') or hasRole('DRIVER')")
    public ResponseEntity<Boolean> isDeliveryItemActive(@PathVariable Long id) {
        log.info("Checking if delivery item is active with id: {}", id);
        boolean active = deliveryItemService.isDeliveryItemActive(id);
        return ResponseEntity.ok(active);
    }

    // Get delivery item statistics
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARTNER')")
    public ResponseEntity<DeliveryItemService.DeliveryItemStatistics> getDeliveryItemStatistics() {
        log.info("Getting delivery item statistics");
        DeliveryItemService.DeliveryItemStatistics response = deliveryItemService.getDeliveryItemStatistics();
        return ResponseEntity.ok(response);
    }
}
