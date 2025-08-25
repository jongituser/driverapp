package org.driver.driverapp.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserResponseDTO {

	private Long id;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private String role;
	private String region;
	private String language;
	private boolean enabled;

	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
}


