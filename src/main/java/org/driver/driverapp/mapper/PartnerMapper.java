package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.PartnerResponseDTO;
import org.driver.driverapp.model.Partner;
import org.springframework.stereotype.Component;

@Component
public class PartnerMapper {

    public PartnerResponseDTO toDTO(Partner partner) {
        return PartnerResponseDTO.builder()
                .id(partner.getId())
                .name(partner.getName())
                .address(partner.getAddress())
                .phone(partner.getPhone())
                .email(partner.getEmail())
                .build();
    }
}
