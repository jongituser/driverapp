package org.driver.driverapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.AdminRegistrationRequestDTO;
import org.driver.driverapp.service.RegistrationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AdminRegistrationRequestDTO request) {
        registrationService.registerAdmin(request);
        return ResponseEntity.ok("Admin user created");
    }


    //TODO
    @EnableMethodSecurity
    @Configuration
    public class MethodSecurityConfig { }
}
