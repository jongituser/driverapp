package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.driver.driverapp.enums.DriverStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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
    @Index(name = "ix_driver_phone_number", columnList = "phone_number"),
    @Index(name = "ix_driver_status", columnList = "status"),
    @Index(name = "ix_driver_user_id", columnList = "user_id")
})
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // 🧍 Personal Information
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    @Size(max = 50)
    private String licenseNumber;

    @Email
    @Size(max = 254)
    private String email;

    private LocalDateTime dateOfBirth;

    // 🖼️ Profile & Identification
    @Size(max = 255)
    private String profileImageUrl;

    @Size(max = 50)
    private String nationalIdNumber;

    @Size(max = 255)
    private String address;

    // 🚗 Vehicle Information
    @Size(max = 50)
    private String vehicleType;

    @Size(max = 20)
    private String vehiclePlateNumber;

    @Size(max = 50)
    private String vehicleColor;

    // 📍 Location & Activity
    private Double latitude;
    private Double longitude;
    private LocalDateTime  lastLocationUpdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Builder.Default
    private int activeDeliveries = 0;
    @Builder.Default
    private int totalDeliveries = 0;

    // 📆 Account Info
    @Builder.Default
    private boolean active = true;

    private LocalDateTime registeredAt;

    private LocalDateTime lastLoginAt;

    // 🧠 Performance Metrics
    @Builder.Default
    private double averageRating = 0.0;
    @Builder.Default
    private int completedDeliveriesToday = 0;

    // 📊 Work status
    @Builder.Default
    private boolean isOnline = false;
    @Builder.Default
    private boolean isAvailableForDelivery = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Reconciliation fields from DashCraft
    @Size(max = 100)
    @Column(length = 100)
    private String zone;

    private Float lastSpeed;

    private Float lastHeading;

    private Instant lastSeenAt;

    private Integer maxCapacity;

    private Integer currentLoad;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;


}
