package org.driver.driverapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customers", indexes = {
        @Index(name = "ix_customer_user_id", columnList = "user_id"),
        @Index(name = "ix_customer_phone", columnList = "phone"),
        @Index(name = "ix_customer_email", columnList = "email"),
        @Index(name = "ix_customer_full_name", columnList = "full_name"),
        @Index(name = "ix_customer_region", columnList = "region")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    @Column(unique = true, length = 254)
    private String email;

    @Size(max = 50, message = "Preferred payment method must not exceed 50 characters")
    @Column(name = "preferred_payment", length = 50)
    private String preferredPayment; // CASH, CARD, MOBILE_MONEY, BANK_TRANSFER



    @Size(max = 100, message = "Region must not exceed 100 characters")
    @Column(length = 100)
    private String region;

    @Column(name = "delivery_preferences", columnDefinition = "TEXT")
    private String deliveryPreferences; // JSON string for flexible preferences

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;



    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Delivery> deliveries = new ArrayList<>();

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Business logic methods

    public void verify() {
        this.verified = true;
    }

    public void unverify() {
        this.verified = false;
    }
}
