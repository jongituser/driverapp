package org.driver.driverapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.PartnerResponseDTO;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.service.PartnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    // 📋 ADMIN: Get all partners
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PartnerResponseDTO>> getAllPartners() {
        return ResponseEntity.ok(partnerService.getAllPartners());
    }

    // 🔍 ADMIN: Get partner by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PartnerResponseDTO> getPartnerById(@PathVariable Long id) {
        return partnerService.getPartnerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ➕ ADMIN: Create partner
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PartnerResponseDTO> createPartner(@RequestBody @Valid Partner partner) {
        return ResponseEntity.ok(partnerService.createPartner(partner));
    }

    // ✏️ ADMIN: Update partner
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PartnerResponseDTO> updatePartner(@PathVariable Long id, @RequestBody @Valid Partner partner) {
        return ResponseEntity.ok(partnerService.updatePartner(id, partner));
    }

    // ❌ ADMIN: Delete partner
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return ResponseEntity.noContent().build();
    }
}
