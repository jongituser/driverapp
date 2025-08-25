package org.driver.driverapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.delivery.request.CreateDeliveryRequestDTO;
import org.driver.driverapp.dto.delivery.request.ProofOfDeliveryDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryResponseDTO;
import org.driver.driverapp.dto.delivery.response.ProofOfDeliveryResponseDTO;
import org.driver.driverapp.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Slf4j
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

    // 📸 Upload proof of delivery
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'PARTNER')")
    @PostMapping("/{id}/proof")
    public ResponseEntity<ProofOfDeliveryResponseDTO> uploadProofOfDelivery(
            @PathVariable Long id,
            @RequestParam("proofOfDeliveryType") String proofOfDeliveryType,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "deliveredLat", required = false) Double deliveredLat,
            @RequestParam(value = "deliveredLong", required = false) Double deliveredLong) {
        
        log.info("Uploading proof of delivery for delivery: {}", id);
        
        ProofOfDeliveryDTO dto = ProofOfDeliveryDTO.builder()
                .deliveryId(id)
                .proofOfDeliveryType(org.driver.driverapp.enums.ProofOfDeliveryType.valueOf(proofOfDeliveryType))
                .file(file)
                .deliveredLat(deliveredLat)
                .deliveredLong(deliveredLong)
                .build();
        
        ProofOfDeliveryResponseDTO response = deliveryService.uploadProofOfDelivery(id, dto);
        return ResponseEntity.ok(response);
    }

    // 📋 Get proof of delivery
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'PARTNER', 'CUSTOMER')")
    @GetMapping("/{id}/proof")
    public ResponseEntity<ProofOfDeliveryResponseDTO> getProofOfDelivery(@PathVariable Long id) {
        log.info("Getting proof of delivery for delivery: {}", id);
        
        ProofOfDeliveryResponseDTO response = deliveryService.getProofOfDelivery(id);
        return ResponseEntity.ok(response);
    }
}
