package org.driver.driverapp.dto.payment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long deliveryId;
    private String deliveryCode;
    private BigDecimal amount;
    private String currency;
    private PaymentProvider provider;
    private PaymentStatus status;
    private String transactionRef;
    private String description;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
}
