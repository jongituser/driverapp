package org.driver.driverapp.dto.geospatial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRouteRequestDTO {
    @NotNull(message = "Pickup address ID is required")
    private Long pickupAddressId;
    
    @NotNull(message = "Dropoff address ID is required")
    private Long dropoffAddressId;
    
    @Builder.Default
    private String transportMode = "driving"; // driving, walking, cycling
    
    @Builder.Default
    private boolean optimizeRoute = true;
    
    @Builder.Default
    private boolean includeTraffic = true;
}
