package org.driver.driverapp.model;

import lombok.Data;

@Data
public class DeliveryRequest {
    private Long pickupPartnerId;
    private String recipientName;
    private String recipientPhone;
    private Address dropoffAddress;


    private Long driverId;
}
