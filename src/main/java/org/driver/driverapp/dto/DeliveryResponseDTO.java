package org.driver.driverapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DeliveryResponseDTO {

    private Long id;
    private String packageDescription;

    private PartnerDTO pickupPartner;
    private PartnerDTO dropoffPartner;

    private DriverResponseDTO driver;

    private OffsetDateTime createdAt;
    private OffsetDateTime deliveredAt;

    private boolean delivered;
}
