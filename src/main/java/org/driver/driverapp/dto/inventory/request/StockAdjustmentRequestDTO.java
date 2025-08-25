package org.driver.driverapp.dto.inventory.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequestDTO {

    @NotNull(message = "Inventory item ID is required")
    private Long inventoryItemId;

    @NotNull(message = "Quantity change is required")
    private Integer quantityChange; // Positive for stock in, negative for stock out

    @NotBlank(message = "Reason is required")
    private String reason; // e.g., "Delivery", "Restock", "Adjustment", "Expiry"

    private String notes;

    @NotNull(message = "Partner ID is required")
    private Long partnerId;
}
