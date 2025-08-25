package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.driver.driverapp.enums.PayoutStatus;
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
    @Index(name = "ix_driver_earning_driver_id", columnList = "driver_id"),
    @Index(name = "ix_driver_earning_delivery_id", columnList = "delivery_id"),
    @Index(name = "ix_driver_earning_payout_status", columnList = "payout_status"),
    @Index(name = "ix_driver_earning_created_at", columnList = "created_at")
})
public class DriverEarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false)
    @Builder.Default
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(name = "payout_reference")
    private String payoutReference;

    @Column(name = "payout_date")
    private Instant payoutDate;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String failureReason;

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
    public boolean isPaid() {
        return PayoutStatus.COMPLETED.equals(payoutStatus);
    }

    public boolean isPending() {
        return PayoutStatus.PENDING.equals(payoutStatus);
    }

    public boolean isProcessing() {
        return PayoutStatus.PROCESSING.equals(payoutStatus);
    }

    public boolean isFailed() {
        return PayoutStatus.FAILED.equals(payoutStatus);
    }

    public void markAsCompleted(String payoutReference) {
        this.payoutStatus = PayoutStatus.COMPLETED;
        this.payoutReference = payoutReference;
        this.payoutDate = Instant.now();
    }

    public void markAsFailed(String failureReason) {
        this.payoutStatus = PayoutStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void markAsProcessing() {
        this.payoutStatus = PayoutStatus.PROCESSING;
    }
}
