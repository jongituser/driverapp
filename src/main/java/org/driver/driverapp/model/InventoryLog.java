package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Partner partner;

    @ManyToOne(optional = false)
    private Product product;

    private int quantityChange;

    private String reason; // e.g., "Restock", "Manual adjustment", "Delivery sent", etc.

    private OffsetDateTime timestamp;

    public void info(String s, String name, String name1, int quantityChange, String reason) {
    }
}
