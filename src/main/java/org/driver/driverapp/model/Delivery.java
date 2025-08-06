package org.driver.driverapp.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.driver.driverapp.enums.DeliveryStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // From where
    @ManyToOne(optional = false)
    private Partner pickupPartner;

    // Assigned driver (nullable until assigned)
    @ManyToOne
    private Driver assignedDriver;

    // Recipient info (kept simple; no separate Customer entity needed)
    private String recipientName;
    private String recipientPhone;

    private Integer etaMinutes;         // planned duration
    private java.time.OffsetDateTime dueAt; // createdAt + eta
    private java.time.OffsetDateTime startedAt; // when driver picks up / starts

    @Embedded
    private Address dropoffAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    private OffsetDateTime createdAt;
    private OffsetDateTime pickedUpAt;
    private OffsetDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime assignedAt;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryItem> items = new ArrayList<>();

    @ManyToOne
    private Driver driver;

    @PrePersist
    void onCreate()
    {
        this.createdAt = OffsetDateTime.now();
    }

}
