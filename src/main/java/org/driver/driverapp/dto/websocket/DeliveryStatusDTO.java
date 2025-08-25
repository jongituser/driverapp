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
public class DeliveryStatusDTO {
    private Long deliveryId;
    private String status;
    private Instant timestamp;
    private String message;
    private Long driverId;
    private String driverName;
    private Long customerId;
    private Long partnerId;
    private String estimatedArrival;
    private String currentLocation;
}
