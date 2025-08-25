package org.driver.driverapp.dto.payment.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class InitiatePaymentRequestDTO {

    @NotNull
    private Long deliveryId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    @Size(max = 3)
    @Builder.Default
    private String currency = "ETB";

    @NotNull
    private PaymentProvider provider;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 1000)
    private String description;

    @Size(max = 1000)
    private String metadata;
}
