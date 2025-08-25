package org.driver.driverapp.dto.delivery.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.ProofOfDeliveryType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfDeliveryDTO {

    @NotNull(message = "Delivery ID is required")
    private Long deliveryId;

    @NotNull(message = "Proof of delivery type is required")
    private ProofOfDeliveryType proofOfDeliveryType;

    @NotNull(message = "Proof file is required")
    private MultipartFile file;

    private Double deliveredLat;

    private Double deliveredLong;
}

