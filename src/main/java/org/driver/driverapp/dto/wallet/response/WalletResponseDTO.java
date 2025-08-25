package org.driver.driverapp.dto.wallet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.WalletOwnerType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDTO {

    private Long id;
    private Long ownerId;
    private WalletOwnerType ownerType;
    private BigDecimal balance;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
