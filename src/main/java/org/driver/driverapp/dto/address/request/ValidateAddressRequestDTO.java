package org.driver.driverapp.dto.address.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class ValidateAddressRequestDTO {

    // GPS Coordinates (Optional for validation)
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    private BigDecimal gpsLat;

    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    private BigDecimal gpsLong;

    // Ethiopian Administrative Structure (Optional for validation)
    private EthiopianRegion region;

    @Size(max = 100, message = "Woreda must not exceed 100 characters")
    private String woreda;

    @Size(max = 100, message = "Kebele must not exceed 100 characters")
    private String kebele;

    // Postal Code (Optional for validation)
    private String postalCode;
}
