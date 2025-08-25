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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "partners", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
}, indexes = {
        @Index(name = "ix_partners_name", columnList = "name"),
        @Index(name = "ix_partners_phone", columnList = "phone"),
        @Index(name = "ix_partners_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Partner name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phone;

    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 255)
    private String logoUrl;

    @Builder.Default
    private boolean active = true;

    // Reconciliation fields from DashCraft
    @Size(max = 100)
    @Column(length = 100)
    private String businessType;

    @Size(max = 100)
    @Column(length = 100)
    private String kebele;

    @Size(max = 100)
    @Column(length = 100)
    private String woreda;

    @Size(max = 100)
    @Column(length = 100)
    private String region;

    private Boolean verified;

    private Float rating;

    private Integer totalOrders;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
