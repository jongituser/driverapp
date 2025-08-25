package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.user.UserResponseDTO;
import org.driver.driverapp.model.User;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class UserMapper {

	public UserResponseDTO toDTO(User user) {
		OffsetDateTime createdAt = user.getCreatedAt() != null ? user.getCreatedAt().atOffset(ZoneOffset.UTC) : null;
		OffsetDateTime updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt().atOffset(ZoneOffset.UTC) : null;
		return UserResponseDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.phone(user.getPhone())
				.fullName(user.getFullName())
				.role(user.getRole() != null ? user.getRole().name() : null)
				.region(user.getRegion())
				.language(user.getLanguage())
				.enabled(user.isEnabled())
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}
}


