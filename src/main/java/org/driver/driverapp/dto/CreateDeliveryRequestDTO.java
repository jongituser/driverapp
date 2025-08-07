package org.driver.driverapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.driver.driverapp.enums.DeliveryStatus;

@Data
public class CreateDeliveryRequestDTO {

    @NotNull(message = "Pickup partner ID is required")
    private Long pickupPartnerId;

    @NotBlank(message = "Dropoff address is required")
    private String dropoffAddress;

    @NotNull(message = "Delivery status is required")
    private DeliveryStatus status;

    private Long driverId; // Optional: only assign if driver is known
}
