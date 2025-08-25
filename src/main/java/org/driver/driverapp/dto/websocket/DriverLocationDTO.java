package org.driver.driverapp.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDTO {
    private Long driverId;
    private Long deliveryId;
    private Double lat;
    private Double longitude;
    private Double speedKmh;
    private Double headingDegrees;
    private Instant timestamp;
    private String driverName;
    private String vehicleInfo;
    private String deliveryStatus;
}
