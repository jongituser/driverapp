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
import org.driver.driverapp.enums.EthiopianRegion;
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
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
    @Index(name = "ix_postal_code_region", columnList = "region"),
    @Index(name = "ix_postal_code_code", columnList = "code"),
    @Index(name = "ix_postal_code_region_code", columnList = "region, code")
})
public class PostalCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EthiopianRegion region;

    @NotBlank
    @Size(max = 10)
    @Column(unique = true, nullable = false, length = 10)
    private String code;

    @Size(max = 255)
    private String description;

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
    public boolean isValid() {
        return active && code != null && !code.trim().isEmpty() && region != null;
    }

    public String getFormattedCode() {
        return code != null ? code.trim() : "";
    }

    public boolean isInRegion(EthiopianRegion targetRegion) {
        return region != null && region.equals(targetRegion);
    }
}
