package org.driver.driverapp.service.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.PaymentProvider;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String userId;
    private String deliveryId;
    private BigDecimal amount;
    private String currency;
    private PaymentProvider provider;
    private String phoneNumber;
    private String description;
    private String metadata;
}
