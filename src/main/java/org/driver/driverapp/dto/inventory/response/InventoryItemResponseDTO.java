package org.driver.driverapp.dto.inventory.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemResponseDTO {

    private Long id;
    private String name;
    private String category;
    private String sku;
    private Integer quantity;
    private String unit;
    private Integer minimumStockThreshold;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private String batchNumber;
    private LocalDate expiryDate;
    private String description;
    private String imageUrl;
    private boolean active;
    private boolean lowStockAlert;
    private boolean expired;
    private Long partnerId;
    private String partnerName;
    private Long supplierId;
    private String supplierName;
    private Instant createdAt;
    private Instant updatedAt;

    // Computed fields
    private boolean isLowStock;
    private boolean isExpiringSoon;
    private int daysUntilExpiry;
}
