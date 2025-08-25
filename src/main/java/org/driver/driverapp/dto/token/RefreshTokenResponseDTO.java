package org.driver.driverapp.dto.token;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RefreshTokenResponseDTO {
	private Long id;
	private String token;
	private boolean revoked;
	private OffsetDateTime expiryDate;
	private Long userId;
}


