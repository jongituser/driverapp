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
public class PartnerUpdateDTO {
    private Long partnerId;
    private Long deliveryId;
    private String updateType; // NEW_DELIVERY, STATUS_CHANGE, DRIVER_ASSIGNED, COMPLETED
    private String message;
    private Instant timestamp;
    private String deliveryStatus;
    private Long driverId;
    private String driverName;
    private String customerName;
    private String deliveryAddress;
    private String estimatedArrival;
    private Double totalAmount;
}
