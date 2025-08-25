package org.driver.driverapp.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private String sku;
    private BigDecimal price;
    private String unit;
    private String description;
    private Boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    // Supplier information
    private Long supplierId;
    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;

    // Computed fields
    private String formattedPrice;
    private Boolean hasSupplier;
}
