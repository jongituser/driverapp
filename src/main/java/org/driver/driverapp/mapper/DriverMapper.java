package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.driver.request.CreateDriverRequestDTO;
import org.driver.driverapp.dto.driver.DriverResponseDTO;
import org.driver.driverapp.dto.driver.UpdateDriverRequestDTO;
import org.driver.driverapp.enums.Role;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverResponseDTO toDTO(Driver driver) {
        return DriverResponseDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .phoneNumber(driver.getPhoneNumber())
                .email(driver.getEmail())
                .licenseNumber(driver.getLicenseNumber())
                .vehicleType(driver.getVehicleType())
                .vehiclePlateNumber(driver.getVehiclePlateNumber())
                .vehicleColor(driver.getVehicleColor())
                .profileImageUrl(driver.getProfileImageUrl())
                .status(driver.getStatus())
                .isOnline(driver.isOnline())
                .totalDeliveries(driver.getTotalDeliveries())
                .activeDeliveries(driver.getActiveDeliveries())
                .build();
    }

    public Driver fromCreateDTO(CreateDriverRequestDTO dto, String encodedPassword) {
        return Driver.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .licenseNumber(dto.getLicenseNumber())
                .vehicleType(dto.getVehicleType())
                .vehiclePlateNumber(dto.getVehiclePlateNumber())
                .vehicleColor(dto.getVehicleColor())
                .profileImageUrl(dto.getProfileImageUrl())
                .isOnline(false)
                .active(true)
                .user(User.builder()
                        .username(dto.getPhoneNumber())
                        .password(encodedPassword)
                        .role(Role.DRIVER)
                        .build())
                .build();
    }

    public void updateDriverFromDTO(Driver driver, UpdateDriverRequestDTO dto) {
        driver.setName(dto.getName());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setEmail(dto.getEmail());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setVehicleType(dto.getVehicleType());
        driver.setVehiclePlateNumber(dto.getVehiclePlateNumber());
        driver.setVehicleColor(dto.getVehicleColor());
        driver.setProfileImageUrl(dto.getProfileImageUrl());
    }
}
