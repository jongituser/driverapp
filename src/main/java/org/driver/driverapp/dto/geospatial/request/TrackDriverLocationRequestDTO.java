package org.driver.driverapp.dto.geospatial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackDriverLocationRequestDTO {
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    @NotNull(message = "Delivery ID is required")
    private Long deliveryId;
    
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double lat;
    
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;
    
    private Double speedKmh;
    
    private Double headingDegrees;
    
    private Double accuracyMeters;
}
