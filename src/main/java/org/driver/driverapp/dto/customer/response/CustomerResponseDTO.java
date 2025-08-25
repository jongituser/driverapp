package org.driver.driverapp.dto.customer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String email;
    private String preferredPayment;
    private Long defaultAddressId;
    private String region;
    private String deliveryPreferences;
    private Boolean active;
    private Boolean verified;
    private Instant createdAt;
    private Instant updatedAt;
}
