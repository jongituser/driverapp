package org.driver.driverapp.dto.inventory.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryItemRequestDTO {

    @Size(max = 200, message = "Item name must not exceed 200 characters")
    private String name;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @Positive(message = "Minimum stock threshold must be positive")
    private Integer minimumStockThreshold;

    private BigDecimal unitPrice;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    private LocalDate expiryDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    private Boolean active;

    private String notes;

    private Long supplierId;
}
