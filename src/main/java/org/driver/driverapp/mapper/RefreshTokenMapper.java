package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.token.RefreshTokenResponseDTO;
import org.driver.driverapp.model.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class RefreshTokenMapper {

	public RefreshTokenResponseDTO toDTO(RefreshToken token) {
		OffsetDateTime expiry = token.getExpiryDate() != null ? token.getExpiryDate().atOffset(ZoneOffset.UTC) : null;
		return RefreshTokenResponseDTO.builder()
				.id(token.getId())
				.token(token.getToken())
				.revoked(token.isRevoked())
				.expiryDate(expiry)
				.userId(token.getUser() != null ? token.getUser().getId() : null)
				.build();
	}
}


