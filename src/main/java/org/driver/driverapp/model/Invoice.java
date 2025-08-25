package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.driver.driverapp.enums.InvoiceStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Index(name = "ix_invoice_partner_id", columnList = "partner_id"),
    @Index(name = "ix_invoice_status", columnList = "status"),
    @Index(name = "ix_invoice_due_date", columnList = "due_date"),
    @Index(name = "ix_invoice_invoice_number", columnList = "invoice_number")
})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @NotBlank
    @Size(max = 50)
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Size(max = 255)
    @Column(name = "payment_reference")
    private String paymentReference;

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
        return InvoiceStatus.PAID.equals(status);
    }

    public boolean isOverdue() {
        return InvoiceStatus.OVERDUE.equals(status);
    }

    public boolean isDraft() {
        return InvoiceStatus.DRAFT.equals(status);
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    public boolean isFullyPaid() {
        return paidAmount.compareTo(totalAmount) >= 0;
    }

    public void markAsPaid(BigDecimal amount, String paymentReference) {
        this.paidAmount = this.paidAmount.add(amount);
        this.paymentReference = paymentReference;
        this.paidDate = LocalDate.now();
        
        if (isFullyPaid()) {
            this.status = InvoiceStatus.PAID;
        }
    }

    public void markAsOverdue() {
        if (!isPaid() && LocalDate.now().isAfter(dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }

    public void markAsSent() {
        this.status = InvoiceStatus.SENT;
    }
}
