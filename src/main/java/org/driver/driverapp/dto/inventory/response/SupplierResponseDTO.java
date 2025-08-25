package org.driver.driverapp.dto.inventory.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String region;
    private boolean active;
    private boolean verified;
    private Long partnerId;
    private String partnerName;
    private Instant createdAt;
    private Instant updatedAt;
}
