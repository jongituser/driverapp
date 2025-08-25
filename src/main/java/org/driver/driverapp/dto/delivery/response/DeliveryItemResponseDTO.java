package org.driver.driverapp.dto.delivery.response;

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
public class DeliveryItemResponseDTO {

    private Long id;
    private Long deliveryId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
    private Boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    // Product information
    private String productName;
    private String productCategory;
    private String productSku;
    private String productUnit;
    private String productDescription;

    // Computed fields
    private String formattedPrice;
    private String formattedTotal;
    private Boolean hasProduct;
}
