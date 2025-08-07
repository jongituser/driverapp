package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.servlet.http.Part;
import lombok.*;
import org.driver.driverapp.enums.DriverStatus;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🧍 Personal Information
    private String name;

    @Column(unique = true)
    private String phoneNumber;

    private String licenseNumber;
    private String email;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    // 🖼️ Profile & Identification
    private String profileImageUrl;
    private String nationalIdNumber;
    private String address;

    // 🚗 Vehicle Information
    private String vehicleType;
    private String vehiclePlateNumber;
    private String vehicleColor;

    // 📍 Location & Activity
    private Double latitude;
    private Double longitude;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLocationUpdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    private int activeDeliveries = 0;
    private int totalDeliveries = 0;

    // 📆 Account Info
    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginAt;

    // 🧠 Performance Metrics
    private double averageRating = 0.0;
    private int completedDeliveriesToday = 0;

    // 📊 Work status
    private boolean isOnline = false;
    private boolean isAvailableForDelivery = true;

    @OneToOne
    @JoinColumn (name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

}
