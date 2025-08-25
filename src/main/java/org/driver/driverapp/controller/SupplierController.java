package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.inventory.request.CreateSupplierRequestDTO;
import org.driver.driverapp.dto.inventory.response.SupplierResponseDTO;
import org.driver.driverapp.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Slf4j
public class SupplierController {

    private final SupplierService supplierService;

    // Create supplier
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<SupplierResponseDTO> createSupplier(
            @Valid @RequestBody CreateSupplierRequestDTO request) {
        log.info("Creating supplier: {}", request.getName());
        SupplierResponseDTO response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get supplier by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<SupplierResponseDTO> getSupplierById(@PathVariable Long id) {
        log.info("Getting supplier with id: {}", id);
        SupplierResponseDTO response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    // Get all suppliers with pagination
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all suppliers with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.getAllSuppliers(pageable);
        return ResponseEntity.ok(response);
    }

    // Get suppliers by partner
    @GetMapping("/partners/{partnerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> getSuppliersByPartner(
            @PathVariable Long partnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting suppliers for partner: {}", partnerId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.getSuppliersByPartner(partnerId, pageable);
        return ResponseEntity.ok(response);
    }

    // Get suppliers by region
    @GetMapping("/region/{region}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> getSuppliersByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting suppliers for region: {}", region);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.getSuppliersByRegion(region, pageable);
        return ResponseEntity.ok(response);
    }

    // Get suppliers by city
    @GetMapping("/city/{city}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> getSuppliersByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting suppliers for city: {}", city);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.getSuppliersByCity(city, pageable);
        return ResponseEntity.ok(response);
    }

    // Get verified suppliers
    @GetMapping("/verified")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> getVerifiedSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting verified suppliers");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.getVerifiedSuppliers(pageable);
        return ResponseEntity.ok(response);
    }

    // Search suppliers by name
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<Page<SupplierResponseDTO>> searchSuppliersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Searching suppliers by name: {}", name);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SupplierResponseDTO> response = supplierService.searchSuppliersByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    // Update supplier
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<SupplierResponseDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierRequestDTO request) {
        log.info("Updating supplier with id: {}", id);
        SupplierResponseDTO response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete supplier
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        log.info("Deleting supplier with id: {}", id);
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    // Verify supplier
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SupplierResponseDTO> verifySupplier(@PathVariable Long id) {
        log.info("Verifying supplier with id: {}", id);
        SupplierResponseDTO response = supplierService.verifySupplier(id);
        return ResponseEntity.ok(response);
    }

    // Unverify supplier
    @PostMapping("/{id}/unverify")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SupplierResponseDTO> unverifySupplier(@PathVariable Long id) {
        log.info("Unverifying supplier with id: {}", id);
        SupplierResponseDTO response = supplierService.unverifySupplier(id);
        return ResponseEntity.ok(response);
    }

    // Get supplier statistics
    @GetMapping("/partners/{partnerId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<SupplierService.SupplierStatistics> getSupplierStatistics(@PathVariable Long partnerId) {
        log.info("Getting supplier statistics for partner: {}", partnerId);
        SupplierService.SupplierStatistics response = supplierService.getSupplierStatistics(partnerId);
        return ResponseEntity.ok(response);
    }
}
