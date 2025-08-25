package org.driver.driverapp.dto.geospatial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofencingAlertRequestDTO {
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    @NotNull(message = "Delivery ID is required")
    private Long deliveryId;
    
    @NotNull(message = "Center latitude is required")
    private Double centerLat;
    
    @NotNull(message = "Center longitude is required")
    private Double centerLong;
    
    @NotNull(message = "Radius in kilometers is required")
    @DecimalMin(value = "0.1", message = "Radius must be at least 0.1 km")
    private Double radiusKm;
    
    @Builder.Default
    private boolean enableAlerts = true;
}
