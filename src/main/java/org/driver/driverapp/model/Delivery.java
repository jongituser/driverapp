package org.driver.driverapp.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import org.driver.driverapp.enums.ProofOfDeliveryType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
        @Index(name = "ix_delivery_code", columnList = "deliveryCode"),
        @Index(name = "ix_delivery_status", columnList = "status"),
        @Index(name = "ix_delivery_driver_id", columnList = "driver_id")
})
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // 📦 Delivery info
    @NotBlank
    @Size(max = 64)
    @Column(length = 64)
    private String deliveryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_partner_id")
    private Partner pickupPartner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_partner_id")
    private Partner dropoffPartner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotBlank
    @Size(max = 32)
    @Column(length = 32)
    private String status; // Consider using an enum later

    private OffsetDateTime pickupTime;
    private OffsetDateTime dropoffTime;

    private double distanceInKm;
    private double price;

    // Proof of Delivery fields
    @Enumerated(EnumType.STRING)
    @Column(name = "proof_of_delivery_type")
    private ProofOfDeliveryType proofOfDeliveryType;

    @Column(name = "proof_of_delivery_url", length = 500)
    private String proofOfDeliveryUrl;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "delivered_lat")
    private Double deliveredLat;

    @Column(name = "delivered_long")
    private Double deliveredLong;

    @Column(nullable = false, length = 255)
    private String dropoffAddress; // ✅ <-- ADD THIS LINE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;

    // Reconciliation fields from DashCraft
    @Size(max = 100)
    @Column(length = 100)
    private String pickupRegion;

    @Size(max = 100)
    @Column(length = 100)
    private String dropoffRegion;

    private Double distanceKm;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<DeliveryItem> deliveryItems = new ArrayList<>();

    // Business logic methods
    public void addDeliveryItem(DeliveryItem item) {
        deliveryItems.add(item);
        item.setDelivery(this);
    }

    public void removeDeliveryItem(DeliveryItem item) {
        deliveryItems.remove(item);
        item.setDelivery(null);
    }

    public BigDecimal getTotalAmount() {
        return deliveryItems.stream()
                .filter(DeliveryItem::isActive)
                .map(DeliveryItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return deliveryItems.stream()
                .filter(DeliveryItem::isActive)
                .mapToInt(DeliveryItem::getQuantity)
                .sum();
    }
}
