package org.driver.driverapp.dto.wallet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.WalletOwnerType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitWalletRequestDTO {

    @NotNull
    private Long ownerId;

    @NotNull
    private WalletOwnerType ownerType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @Size(max = 255)
    private String reference;

    @Size(max = 1000)
    private String description;

    @Size(max = 1000)
    private String metadata;
}
