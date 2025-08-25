package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.admin.AdminRegistrationRequestDTO;
import org.driver.driverapp.dto.driver.request.CreateDriverRequestDTO;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.enums.Role;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerNewDriver(CreateDriverRequestDTO request) {
        // 1. Create User with hashed password
        User user = User.builder()
                .username(request.getPhoneNumber()) // or use email/username
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.DRIVER)
                .enabled(true)
                .build();

        // 2. Create Driver and link to User
        Driver driver = Driver.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .licenseNumber(request.getLicenseNumber())
                .vehicleType(request.getVehicleType())
                .vehiclePlateNumber(request.getVehiclePlateNumber())
                .vehicleColor(request.getVehicleColor())
                .profileImageUrl(request.getProfileImageUrl())
                .status(DriverStatus.AVAILABLE)
                .user(user)
                .build();

        user.setDriver(driver);

        // 3. Save user (and driver via cascade)
        userRepository.save(user);
    }

    public void registerAdmin(AdminRegistrationRequestDTO request) {
        if (request.getRole() == Role.DRIVER) {
            throw new IllegalArgumentException("Use normal registration for drivers");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}
