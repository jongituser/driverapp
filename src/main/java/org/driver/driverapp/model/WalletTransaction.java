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
import org.driver.driverapp.enums.TransactionType;
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
    @Index(name = "ix_wallet_transaction_wallet_id", columnList = "wallet_id"),
    @Index(name = "ix_wallet_transaction_type", columnList = "transaction_type"),
    @Index(name = "ix_wallet_transaction_created_at", columnList = "created_at"),
    @Index(name = "ix_wallet_transaction_reference", columnList = "reference")
})
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "balance_before", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    @Size(max = 255)
    @Column(name = "reference")
    private String reference;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String metadata;

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
    public boolean isCredit() {
        return TransactionType.CREDIT.equals(transactionType);
    }

    public boolean isDebit() {
        return TransactionType.DEBIT.equals(transactionType);
    }

    public BigDecimal getNetAmount() {
        return isCredit() ? amount : amount.negate();
    }
}
