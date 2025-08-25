package org.driver.driverapp.dto.geospatial.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.driver.driverapp.enums.GeofenceStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofencingAlertResponseDTO {
    private Long alertId;
    private Long driverId;
    private Long deliveryId;
    private GeofenceStatus status;
    private Double driverLat;
    private Double driverLong;
    private Double zoneCenterLat;
    private Double zoneCenterLong;
    private Double zoneRadiusKm;
    private Double distanceFromZoneKm;
    private Instant timestamp;
    private String alertMessage;
    private Boolean isActive;
}
