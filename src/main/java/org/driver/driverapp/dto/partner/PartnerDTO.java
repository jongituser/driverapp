package org.driver.driverapp.dto.partner;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerDTO {

	private Long id;
	private String name;
	private String address;
	private String phone;
	private String email;
}
