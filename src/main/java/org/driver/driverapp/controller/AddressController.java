package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.address.request.CreateAddressRequestDTO;
import org.driver.driverapp.dto.address.request.UpdateAddressRequestDTO;
import org.driver.driverapp.dto.address.request.ValidateAddressRequestDTO;
import org.driver.driverapp.dto.address.response.AddressResponseDTO;
import org.driver.driverapp.dto.address.response.AddressValidationResponseDTO;
import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDTO> createAddress(@Valid @RequestBody CreateAddressRequestDTO requestDTO) {
        log.info("Creating address for customer: {}, partner: {}", requestDTO.getCustomerId(), requestDTO.getPartnerId());
        
        AddressResponseDTO response = addressService.createAddress(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequestDTO requestDTO) {
        log.info("Updating address: {}", id);
        
        AddressResponseDTO response = addressService.updateAddress(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.info("Deleting address: {}", id);
        
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        AddressResponseDTO response = addressService.getAddressById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #customerId == authentication.principal.id")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByCustomer(@PathVariable Long customerId) {
        List<AddressResponseDTO> response = addressService.getAddressesByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #customerId == authentication.principal.id")
    public ResponseEntity<Page<AddressResponseDTO>> getAddressesByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AddressResponseDTO> response = addressService.getAddressesByCustomer(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByPartner(@PathVariable Long partnerId) {
        List<AddressResponseDTO> response = addressService.getAddressesByPartner(partnerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partner/{partnerId}/page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<AddressResponseDTO>> getAddressesByPartner(
            @PathVariable Long partnerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AddressResponseDTO> response = addressService.getAddressesByPartner(partnerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/region/{region}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByRegion(@PathVariable EthiopianRegion region) {
        List<AddressResponseDTO> response = addressService.getAddressesByRegion(region);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/woreda/{woreda}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByWoreda(@PathVariable String woreda) {
        List<AddressResponseDTO> response = addressService.getAddressesByWoreda(woreda);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kebele/{kebele}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByKebele(@PathVariable String kebele) {
        List<AddressResponseDTO> response = addressService.getAddressesByKebele(kebele);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/radius")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesWithinRadius(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam BigDecimal radius) {
        List<AddressResponseDTO> response = addressService.getAddressesWithinRadius(lat, lng, radius);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressValidationResponseDTO> validateAddress(@Valid @RequestBody ValidateAddressRequestDTO requestDTO) {
        log.info("Validating address");
        
        AddressValidationResponseDTO response = addressService.validateAddress(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/gps-only")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getGpsOnlyAddresses() {
        List<AddressResponseDTO> response = addressService.getGpsOnlyAddresses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/full-ethiopian")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getFullEthiopianAddresses() {
        List<AddressResponseDTO> response = addressService.getFullEthiopianAddresses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/hybrid")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AddressResponseDTO>> getHybridAddresses() {
        List<AddressResponseDTO> response = addressService.getHybridAddresses();
        return ResponseEntity.ok(response);
    }
}
