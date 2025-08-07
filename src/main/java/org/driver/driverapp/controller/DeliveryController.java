package org.driver.driverapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.CreateDeliveryRequestDTO;
import org.driver.driverapp.dto.DeliveryResponseDTO;
import org.driver.driverapp.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 🔽 Get all deliveries
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    // ➕ Create new delivery
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@RequestBody @Valid CreateDeliveryRequestDTO dto) {
        return ResponseEntity.ok(deliveryService.createDelivery(dto));
    }

    // 🔍 Get delivery by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ Delete delivery
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
