package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.driver.driverapp.enums.EthiopianRegion;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
    @Index(name = "ix_address_customer_id", columnList = "customer_id"),
    @Index(name = "ix_address_partner_id", columnList = "partner_id"),
    @Index(name = "ix_address_gps", columnList = "gps_lat, gps_long"),
    @Index(name = "ix_address_region", columnList = "region"),
    @Index(name = "ix_address_postal_code", columnList = "postal_code_id")
})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // GPS Coordinates (Required)
    @NotNull
    @DecimalMin(value = "-90.0", inclusive = true)
    @DecimalMax(value = "90.0", inclusive = true)
    @Column(name = "gps_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal gpsLat;

    @NotNull
    @DecimalMin(value = "-180.0", inclusive = true)
    @DecimalMax(value = "180.0", inclusive = true)
    @Column(name = "gps_long", nullable = false, precision = 11, scale = 8)
    private BigDecimal gpsLong;

    // Ethiopian Administrative Structure (Optional)
    @Enumerated(EnumType.STRING)
    private EthiopianRegion region;

    @Size(max = 100)
    private String woreda;

    @Size(max = 100)
    private String kebele;

    // Postal Code (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postal_code_id", referencedColumnName = "id")
    private PostalCode postalCode;

    // Description (Free text)
    @Size(max = 500)
    private String description;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;

    @Builder.Default
    private boolean active = true;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Business methods
    public boolean hasGpsCoordinates() {
        return gpsLat != null && gpsLong != null;
    }

    public boolean hasEthiopianAddress() {
        return region != null && woreda != null && kebele != null;
    }

    public boolean hasPostalCode() {
        return postalCode != null && postalCode.isValid();
    }

    public boolean isValid() {
        // At least GPS OR (region + woreda + kebele) must be provided
        return hasGpsCoordinates() || hasEthiopianAddress();
    }

    public boolean isGpsOnly() {
        return hasGpsCoordinates() && !hasEthiopianAddress();
    }

    public boolean isFullEthiopianAddress() {
        return hasGpsCoordinates() && hasEthiopianAddress();
    }

    public boolean isCustomerAddress() {
        return customer != null;
    }

    public boolean isPartnerAddress() {
        return partner != null;
    }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (hasEthiopianAddress()) {
            sb.append(kebele).append(", ").append(woreda).append(", ").append(region.getFullName());
        }
        
        if (hasPostalCode()) {
            sb.append(" ").append(postalCode.getFormattedCode());
        }
        
        if (description != null && !description.trim().isEmpty()) {
            sb.append(" (").append(description).append(")");
        }
        
        return sb.toString();
    }

    public String getGpsCoordinates() {
        if (hasGpsCoordinates()) {
            return String.format("%.8f, %.8f", gpsLat, gpsLong);
        }
        return null;
    }

    public boolean isInRegion(EthiopianRegion targetRegion) {
        return region != null && region.equals(targetRegion);
    }

    public boolean isInWoreda(String targetWoreda) {
        return woreda != null && woreda.equalsIgnoreCase(targetWoreda);
    }

    public boolean isInKebele(String targetKebele) {
        return kebele != null && kebele.equalsIgnoreCase(targetKebele);
    }
}
