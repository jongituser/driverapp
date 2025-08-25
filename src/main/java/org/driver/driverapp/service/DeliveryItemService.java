package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryItemService {

    private final DeliveryItemRepository deliveryItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductRepository productRepository;
    private final DeliveryItemMapper deliveryItemMapper;

    // Create delivery item
    public DeliveryItemResponseDTO createDeliveryItem(CreateDeliveryItemRequestDTO requestDTO) {
        log.info("Creating delivery item for delivery: {} and product: {}", 
                requestDTO.getDeliveryId(), requestDTO.getProductId());

        // Validate delivery exists
        Delivery delivery = deliveryRepository.findById(requestDTO.getDeliveryId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + requestDTO.getDeliveryId()));

        // Validate product exists and is active
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + requestDTO.getProductId()));
        
        if (!product.isActive()) {
            throw new IllegalArgumentException("Cannot add inactive product to delivery");
        }

        // Check if delivery item already exists for this delivery and product
        if (deliveryItemRepository.existsByDeliveryIdAndProductIdAndActiveTrue(
                requestDTO.getDeliveryId(), requestDTO.getProductId())) {
            throw new IllegalArgumentException("Delivery item already exists for this delivery and product");
        }

        // Create delivery item
        DeliveryItem deliveryItem = deliveryItemMapper.toEntity(requestDTO);
        deliveryItem.setDelivery(delivery);
        deliveryItem.setProduct(product);
        deliveryItem.setActive(true);
        deliveryItem.calculateTotal();

        DeliveryItem savedDeliveryItem = deliveryItemRepository.save(deliveryItem);
        log.info("Created delivery item with id: {}", savedDeliveryItem.getId());

        return deliveryItemMapper.toResponseDTO(savedDeliveryItem);
    }

    // Get delivery item by ID
    @Transactional(readOnly = true)
    public DeliveryItemResponseDTO getDeliveryItemById(Long id) {
        log.info("Getting delivery item by id: {}", id);
        DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found with id: " + id));
        return deliveryItemMapper.toResponseDTO(deliveryItem);
    }

    // Update delivery item
    public DeliveryItemResponseDTO updateDeliveryItem(Long id, UpdateDeliveryItemRequestDTO requestDTO) {
        log.info("Updating delivery item with id: {}", id);
        DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found with id: " + id));

        deliveryItemMapper.updateEntityFromDto(requestDTO, deliveryItem);
        
        // Recalculate total if quantity or price changed
        if (requestDTO.getQuantity() != null || requestDTO.getPrice() != null) {
            deliveryItem.calculateTotal();
        }

        DeliveryItem updatedDeliveryItem = deliveryItemRepository.save(deliveryItem);
        log.info("Updated delivery item with id: {}", updatedDeliveryItem.getId());

        return deliveryItemMapper.toResponseDTO(updatedDeliveryItem);
    }

    // Delete delivery item (soft delete)
    public void deleteDeliveryItem(Long id) {
        log.info("Deleting delivery item with id: {}", id);
        DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found with id: " + id));
        deliveryItem.deactivate();
        deliveryItemRepository.save(deliveryItem);
        log.info("Deleted delivery item with id: {}", id);
    }

    // Get delivery items by delivery
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByDelivery(Long deliveryId) {
        log.info("Getting delivery items for delivery: {}", deliveryId);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByDeliveryIdAndActiveTrue(deliveryId);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Get delivery items by product
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByProduct(Long productId) {
        log.info("Getting delivery items for product: {}", productId);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByProductIdAndActiveTrue(productId);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Get delivery items by product category
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByProductCategory(String category) {
        log.info("Getting delivery items for product category: {}", category);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByProductCategory(category);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Get delivery items by product supplier
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByProductSupplier(Long supplierId) {
        log.info("Getting delivery items for product supplier: {}", supplierId);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByProductSupplierId(supplierId);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Get all active delivery items
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getAllActiveDeliveryItems() {
        log.info("Getting all active delivery items");
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByActiveTrue();
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Add product to delivery (convenience method)
    public DeliveryItemResponseDTO addProductToDelivery(Long deliveryId, Long productId, Integer quantity, BigDecimal price) {
        CreateDeliveryItemRequestDTO requestDTO = CreateDeliveryItemRequestDTO.builder()
                .deliveryId(deliveryId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
        return createDeliveryItem(requestDTO);
    }

    // Remove product from delivery
    public void removeProductFromDelivery(Long deliveryId, Long productId) {
        log.info("Removing product {} from delivery {}", productId, deliveryId);
        DeliveryItem deliveryItem = deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(deliveryId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found for delivery: " + deliveryId + " and product: " + productId));
        deleteDeliveryItem(deliveryItem.getId());
    }

    // Update quantity of product in delivery
    public DeliveryItemResponseDTO updateProductQuantity(Long deliveryId, Long productId, Integer newQuantity) {
        log.info("Updating quantity of product {} in delivery {} to {}", productId, deliveryId, newQuantity);
        DeliveryItem deliveryItem = deliveryItemRepository.findByDeliveryIdAndProductIdAndActiveTrue(deliveryId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found for delivery: " + deliveryId + " and product: " + productId));
        
        UpdateDeliveryItemRequestDTO requestDTO = UpdateDeliveryItemRequestDTO.builder()
                .quantity(newQuantity)
                .build();
        
        return updateDeliveryItem(deliveryItem.getId(), requestDTO);
    }

    // Get delivery total amount
    @Transactional(readOnly = true)
    public BigDecimal getDeliveryTotalAmount(Long deliveryId) {
        log.info("Getting total amount for delivery: {}", deliveryId);
        BigDecimal total = deliveryItemRepository.getTotalAmountByDelivery(deliveryId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Get delivery total quantity
    @Transactional(readOnly = true)
    public Integer getDeliveryTotalQuantity(Long deliveryId) {
        log.info("Getting total quantity for delivery: {}", deliveryId);
        Integer total = deliveryItemRepository.getTotalQuantityByDelivery(deliveryId);
        return total != null ? total : 0;
    }

    // Get delivery items with total amount greater than threshold
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByTotalThreshold(BigDecimal threshold) {
        log.info("Getting delivery items with total amount greater than: {}", threshold);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByTotalGreaterThan(threshold);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Get delivery items by price range
    @Transactional(readOnly = true)
    public List<DeliveryItemResponseDTO> getDeliveryItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Getting delivery items by price range: {} - {}", minPrice, maxPrice);
        List<DeliveryItem> deliveryItems = deliveryItemRepository.findByPriceBetween(minPrice, maxPrice);
        return deliveryItemMapper.toResponseDTOList(deliveryItems);
    }

    // Activate delivery item
    public DeliveryItemResponseDTO activateDeliveryItem(Long id) {
        log.info("Activating delivery item with id: {}", id);
        DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found with id: " + id));
        deliveryItem.activate();
        DeliveryItem activatedDeliveryItem = deliveryItemRepository.save(deliveryItem);
        log.info("Activated delivery item with id: {}", activatedDeliveryItem.getId());
        return deliveryItemMapper.toResponseDTO(activatedDeliveryItem);
    }

    // Deactivate delivery item
    public DeliveryItemResponseDTO deactivateDeliveryItem(Long id) {
        log.info("Deactivating delivery item with id: {}", id);
        DeliveryItem deliveryItem = deliveryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery item not found with id: " + id));
        deliveryItem.deactivate();
        DeliveryItem deactivatedDeliveryItem = deliveryItemRepository.save(deliveryItem);
        log.info("Deactivated delivery item with id: {}", deactivatedDeliveryItem.getId());
        return deliveryItemMapper.toResponseDTO(deactivatedDeliveryItem);
    }

    // Check if delivery item exists
    @Transactional(readOnly = true)
    public boolean deliveryItemExists(Long id) {
        return deliveryItemRepository.existsById(id);
    }

    // Check if delivery item is active
    @Transactional(readOnly = true)
    public boolean isDeliveryItemActive(Long id) {
        return deliveryItemRepository.findById(id)
                .map(DeliveryItem::isActive)
                .orElse(false);
    }

    // Get delivery item statistics
    @Transactional(readOnly = true)
    public DeliveryItemStatistics getDeliveryItemStatistics() {
        log.info("Getting delivery item statistics");
        long totalDeliveryItems = deliveryItemRepository.countActiveDeliveryItems();

        return DeliveryItemStatistics.builder()
                .totalDeliveryItems(totalDeliveryItems)
                .build();
    }

    // Inner class for statistics
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DeliveryItemStatistics {
        private long totalDeliveryItems;
    }
}
