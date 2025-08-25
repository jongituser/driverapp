package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.partner.response.PartnerResponseDTO;
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
				.businessType(partner.getBusinessType())
				.kebele(partner.getKebele())
				.woreda(partner.getWoreda())
				.city(partner.getCity())
				.region(partner.getRegion())
				.verified(partner.getVerified())
				.rating(partner.getRating())
				.totalOrders(partner.getTotalOrders())
				.build();
	}
}
