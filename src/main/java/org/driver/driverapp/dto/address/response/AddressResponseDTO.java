package org.driver.driverapp.dto.address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.EthiopianRegion;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private Long id;
    
    // GPS Coordinates
    private BigDecimal gpsLat;
    private BigDecimal gpsLong;
    private String gpsCoordinates;
    
    // Ethiopian Administrative Structure
    private EthiopianRegion region;
    private String woreda;
    private String kebele;
    
    // Postal Code
    private Long postalCodeId;
    private String postalCode;
    
    // Description
    private String description;
    
    // Formatted Address
    private String formattedAddress;
    
    // Owner Information
    private Long customerId;
    private Long partnerId;
    
    // Address Type
    private String addressType; // "GPS_ONLY", "FULL_ETHIOPIAN", "HYBRID"
    
    // Metadata
    private boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}
