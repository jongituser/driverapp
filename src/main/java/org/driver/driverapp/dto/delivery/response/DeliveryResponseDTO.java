package org.driver.driverapp.dto.delivery.response;

import lombok.Builder;
import lombok.Data;
import org.driver.driverapp.dto.partner.PartnerDTO;
import org.driver.driverapp.dto.driver.DriverResponseDTO;

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
