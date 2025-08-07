package org.driver.driverapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
}
