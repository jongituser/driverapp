package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📦 Delivery info
    private String deliveryCode;

    @ManyToOne
    private Partner pickupPartner;

    @ManyToOne
    private Partner dropoffPartner;

    @ManyToOne
    private Driver driver;

    private String status; // Consider using an enum later

    private OffsetDateTime pickupTime;
    private OffsetDateTime dropoffTime;

    private double distanceInKm;
    private double price;

    @Column(nullable = false)
    private String dropoffAddress; // ✅ <-- ADD THIS LINE

    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;
}
