package org.driver.driverapp.dto.address.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.EthiopianRegion;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequestDTO {

    // GPS Coordinates (Required)
    @NotNull(message = "GPS latitude is required")
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    private BigDecimal gpsLat;

    @NotNull(message = "GPS longitude is required")
    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    private BigDecimal gpsLong;

    // Ethiopian Administrative Structure (Optional)
    private EthiopianRegion region;

    @Size(max = 100, message = "Woreda must not exceed 100 characters")
    private String woreda;

    @Size(max = 100, message = "Kebele must not exceed 100 characters")
    private String kebele;

    // Postal Code (Optional)
    private Long postalCodeId;

    // Description (Free text)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
