package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String phoneNumber;

    private String licenseNumber;

    // TODO Hash later
    // simple auth placeholder (hash later)
    private String password;
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private DriverStatus status = DriverStatus.AVAILABLE;

    //TODO
    private int activeDeliveries = 0; // simple load metric

}
