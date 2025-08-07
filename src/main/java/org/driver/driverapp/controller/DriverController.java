package org.driver.driverapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.CreateDriverRequestDTO;
import org.driver.driverapp.dto.DriverResponseDTO;
import org.driver.driverapp.dto.UpdateDriverRequestDTO;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // 📋 ADMIN: Get all drivers
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.findAllDrivers());
    }

    // 🔍 ADMIN: Search drivers
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<DriverResponseDTO>> searchDrivers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) Boolean isOnline,
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(
                driverService.searchDrivers(name, status, phone, vehicleType, isOnline, active)
        );
    }

    // 🔎 ADMIN: Get driver by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        return driverService.findDriverById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 👤 DRIVER: Get own profile
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/me")
    public ResponseEntity<DriverResponseDTO> getOwnProfile(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(driverService.findByUserUsername(username));
    }

    // ➕ ADMIN: Create driver
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@RequestBody @Valid CreateDriverRequestDTO dto) {
        return ResponseEntity.ok(driverService.createDriver(dto));
    }

    // ✏️ ADMIN: Update driver
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(@PathVariable Long id, @RequestBody @Valid UpdateDriverRequestDTO dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    // ❌ ADMIN: Delete driver
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
