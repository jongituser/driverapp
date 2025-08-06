package org.driver.driverapp.repository;

import org.driver.driverapp.model.InventoryItem;
import org.driver.driverapp.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findByPartnerId(Long partnerId);
    Optional<InventoryItem> findByPartnerIdAndProductId(Long partnerId, Long productId);

}
