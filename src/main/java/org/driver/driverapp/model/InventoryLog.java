package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "inventory_logs", indexes = {
        @Index(name = "ix_inventory_log_item_id", columnList = "inventory_item_id"),
        @Index(name = "ix_inventory_log_type", columnList = "log_type"),
        @Index(name = "ix_inventory_log_created_at", columnList = "created_at"),
        @Index(name = "ix_inventory_log_partner_id", columnList = "partner_id")
})
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LogType logType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @NotNull
    @Column(nullable = false)
    private Integer quantityBefore;

    @NotNull
    @Column(nullable = false)
    private Integer quantityAfter;

    @NotNull
    @Column(nullable = false)
    private Integer quantityChanged;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalValue;

    @Size(max = 200)
    @Column(length = 200)
    private String reason; // e.g., "Delivery", "Restock", "Adjustment", "Expiry"

    @Size(max = 500)
    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner; // Location where the change occurred

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // User who made the change

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public enum LogType {
        STOCK_IN,           // Adding stock (restock, delivery)
        STOCK_OUT,          // Removing stock (delivery, consumption)
        STOCK_ADJUSTMENT,   // Manual adjustment
        STOCK_TRANSFER,     // Moving between locations
        EXPIRY_WRITE_OFF,   // Removing expired items
        DAMAGE_WRITE_OFF,   // Removing damaged items
        INITIAL_STOCK       // Initial stock setup
    }

    // Business logic methods
    public boolean isStockIn() {
        return logType == LogType.STOCK_IN || logType == LogType.INITIAL_STOCK;
    }

    public boolean isStockOut() {
        return logType == LogType.STOCK_OUT || logType == LogType.EXPIRY_WRITE_OFF || logType == LogType.DAMAGE_WRITE_OFF;
    }

    public boolean isAdjustment() {
        return logType == LogType.STOCK_ADJUSTMENT;
    }

    public boolean isTransfer() {
        return logType == LogType.STOCK_TRANSFER;
    }
}
