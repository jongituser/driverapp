package org.driver.driverapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequestDTO {

    @NotNull
    private Long pickupPartnerId;

    @NotNull
    private Long dropoffPartnerId;

    @NotNull
    private Long driverId;

    @NotNull
    private String packageDescription;
}
