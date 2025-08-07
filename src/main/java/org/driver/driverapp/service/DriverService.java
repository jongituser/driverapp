package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.CreateDriverRequestDTO;
import org.driver.driverapp.dto.DriverResponseDTO;
import org.driver.driverapp.dto.UpdateDriverRequestDTO;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.mapper.DriverMapper;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.security.PasswordEncoderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final DriverMapper driverMapper;

    public DriverResponseDTO findByUserUsername(String username) {
        Driver driver = driverRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Driver not found for username: " + username));
        return driverMapper.toDTO(driver);
    }

    public List<DriverResponseDTO> searchDrivers(String name,
                                                 DriverStatus status,
                                                 String phone,
                                                 String vehicleType,
                                                 Boolean isOnline,
                                                 Boolean active) {
        return driverRepository.searchDrivers(name, status, phone, vehicleType, isOnline, active)
                .stream()
                .map(driverMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DriverResponseDTO> findAllDrivers() {
        return driverRepository.findAll().stream()
                .map(driverMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<DriverResponseDTO> findDriverById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toDTO);
    }

    public DriverResponseDTO createDriver(CreateDriverRequestDTO dto) {
        String encodedPassword = passwordEncoderService.encode(dto.getPassword());
        Driver driver = driverMapper.fromCreateDTO(dto, encodedPassword);
        return driverMapper.toDTO(driverRepository.save(driver));
    }

    public DriverResponseDTO updateDriver(Long id, UpdateDriverRequestDTO dto) {
        return driverRepository.findById(id)
                .map(driver -> {
                    driverMapper.updateDriverFromDTO(driver, dto);
                    return driverMapper.toDTO(driverRepository.save(driver));
                })
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + id));
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }
}
