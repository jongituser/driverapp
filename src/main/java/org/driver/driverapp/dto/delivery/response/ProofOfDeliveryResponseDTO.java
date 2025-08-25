package org.driver.driverapp.dto.delivery.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.ProofOfDeliveryType;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfDeliveryResponseDTO {

    private Long deliveryId;
    private ProofOfDeliveryType proofOfDeliveryType;
    private String proofOfDeliveryUrl;
    private OffsetDateTime deliveredAt;
    private Double deliveredLat;
    private Double deliveredLong;
}

