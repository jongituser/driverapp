package org.driver.driverapp.repository;

import org.driver.driverapp.model.InventoryItem;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByPartnerAndProduct(Partner partner, Product product);
    List<InventoryItem> findByPartner(Partner partner);
    Optional<InventoryItem> findByPartnerIdAndProductId(Long partnerId, Long productId);
    List<InventoryItem> findByPartnerId(Long partnerId);
}
