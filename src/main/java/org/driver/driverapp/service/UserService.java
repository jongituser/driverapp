package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.token.RegisterUserRequestDTO;
import org.driver.driverapp.enums.Role;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final DriverRepository driverRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public void registerUser(RegisterUserRequestDTO request) {
		// Check if username or phone already exists (optional)
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		// Create User
		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.email(request.getEmail())
				.role(Role.DRIVER)
				.enabled(true)
				.build();

		// Create Driver and link to user
		Driver driver = Driver.builder()
				.name(request.getName())
				.phoneNumber(request.getPhoneNumber())
				.licenseNumber(request.getLicenseNumber())
				.user(user)
				.build();

		// Persist both
		userRepository.save(user);
		driverRepository.save(driver);
	}
}
