package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "inventory_items", indexes = {
        @Index(name = "ix_inventory_sku", columnList = "sku"),
        @Index(name = "ix_inventory_name", columnList = "name"),
        @Index(name = "ix_inventory_category", columnList = "category"),
        @Index(name = "ix_inventory_partner_id", columnList = "partner_id"),
        @Index(name = "ix_inventory_supplier_id", columnList = "supplier_id"),
        @Index(name = "ix_inventory_expiry_date", columnList = "expiry_date"),
        @Index(name = "ix_inventory_batch_number", columnList = "batch_number"),
        @Index(name = "ix_inventory_quantity", columnList = "quantity")
})
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String category;

    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String unit; // e.g., "pieces", "kg", "liters", "boxes"

    @NotNull(message = "Minimum stock threshold is required")
    @Positive(message = "Minimum stock threshold must be positive")
    @Column(nullable = false)
    private Integer minimumStockThreshold;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalValue;

    @Size(max = 100)
    @Column(length = 100)
    private String batchNumber;

    private LocalDate expiryDate;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Size(max = 255)
    @Column(length = 255)
    private String imageUrl;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean lowStockAlert = false;

    @Builder.Default
    private boolean expired = false;

    // Multi-location support
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner; // Warehouse/Location

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Business logic methods
    public boolean isLowStock() {
        return quantity <= minimumStockThreshold;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        return expiryDate != null && 
               expiryDate.isAfter(LocalDate.now()) && 
               expiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public void updateStockLevel(int newQuantity) {
        this.quantity = newQuantity;
        this.lowStockAlert = isLowStock();
        this.expired = isExpired();
        
        // Update total value if unit price is available
        if (this.unitPrice != null) {
            this.totalValue = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public void addStock(int quantityToAdd) {
        updateStockLevel(this.quantity + quantityToAdd);
    }

    public void removeStock(int quantityToRemove) {
        if (quantityToRemove > this.quantity) {
            throw new IllegalArgumentException("Cannot remove more stock than available");
        }
        updateStockLevel(this.quantity - quantityToRemove);
    }
}
