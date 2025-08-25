package org.driver.driverapp.dto.partner.response;

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

    private String businessType;
    private String kebele;
    private String woreda;
    private String city;
    private String region;
    private Boolean verified;
    private Float rating;
    private Integer totalOrders;
}



