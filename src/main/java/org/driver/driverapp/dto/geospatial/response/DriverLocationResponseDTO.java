package org.driver.driverapp.dto.geospatial.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationResponseDTO {
    private Long driverId;
    private Long deliveryId;
    private Double lat;
    private Double longitude;
    private Instant timestamp;
    private Double speedKmh;
    private Double headingDegrees;
    private Double accuracyMeters;
    private String driverName;
    private String vehicleInfo;
    private String deliveryStatus;
    private Boolean isOnline;
    private Instant lastUpdated;
}
