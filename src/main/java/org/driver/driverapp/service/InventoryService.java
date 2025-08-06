package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.model.*;
import org.driver.driverapp.repository.*;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryItemRepository inventoryRepo;
    private final InventoryLogRepository logRepo;
    private final PartnerRepository partnerRepo;
    private final ProductRepository productRepo;

    // Updates the stock for a given partner + product, and logs the change
    public void updateStock(Long partnerId, Long productId, int quantityChange, String reason) {
        Partner partner = partnerRepo.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        InventoryItem item = inventoryRepo.findByPartnerIdAndProductId(partnerId, productId)
                .orElse(InventoryItem.builder()
                        .partner(partner)
                        .product(product)
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + quantityChange);
        inventoryRepo.save(item);

        InventoryLog log = InventoryLog.builder()
                .partner(partner)
                .product(product)
                .quantityChange(quantityChange)
                .reason(reason)
                .timestamp(OffsetDateTime.now())
                .build();

        logRepo.save(log);
        log.info("Inventory updated: Partner={}, Product={}, Change={}, Reason={}",
                partner.getName(), product.getName(), quantityChange, reason);
    }

    // Returns all inventory items for a specific partner
    public List<InventoryItem> getInventoryByPartner(Long partnerId) {
        return inventoryRepo.findByPartnerId(partnerId);
    }

    // Returns all inventory logs for a specific partner
    public List<InventoryLog> getLogsByPartner(Long partnerId) {
        return logRepo.findByPartnerId(partnerId);
    }
}
