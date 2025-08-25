package org.driver.driverapp.dto.address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.EthiopianRegion;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostalCodeResponseDTO {

    private Long id;
    private EthiopianRegion region;
    private String code;
    private String description;
    private boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}
