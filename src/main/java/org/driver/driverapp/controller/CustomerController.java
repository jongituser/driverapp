package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.customer.request.CreateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.request.UpdateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.request.UpdateDeliveryPreferencesRequestDTO;
import org.driver.driverapp.dto.customer.response.CustomerResponseDTO;
import org.driver.driverapp.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Customer endpoints

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CreateCustomerRequestDTO request) {
        CustomerResponseDTO customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER') or #userId == authentication.principal.id")
    public ResponseEntity<CustomerResponseDTO> getCustomerByUserId(@PathVariable Long userId) {
        CustomerResponseDTO customer = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/region/{region}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    public ResponseEntity<Page<CustomerResponseDTO>> getCustomersByRegion(
            @PathVariable String region, Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.getCustomersByRegion(region, pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/verified")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<CustomerResponseDTO>> getVerifiedCustomers(Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.getVerifiedCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomers(
            @RequestParam String searchTerm, Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.searchCustomers(searchTerm, pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomersByName(
            @RequestParam String name, Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.searchCustomersByName(name, pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/phone")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('DRIVER')")
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomersByPhone(
            @RequestParam String phone, Pageable pageable) {
        Page<CustomerResponseDTO> customers = customerService.searchCustomersByPhone(phone, pageable);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == authentication.principal.customerId")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id, @Valid @RequestBody UpdateCustomerRequestDTO request) {
        CustomerResponseDTO customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerResponseDTO> verifyCustomer(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.verifyCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/{id}/unverify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerResponseDTO> unverifyCustomer(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.unverifyCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == authentication.principal.customerId")
    public ResponseEntity<CustomerResponseDTO> updateDeliveryPreferences(
            @PathVariable Long id, @Valid @RequestBody UpdateDeliveryPreferencesRequestDTO request) {
        CustomerResponseDTO customer = customerService.updateDeliveryPreferences(id, request);
        return ResponseEntity.ok(customer);
    }



    // Statistics endpoint

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<CustomerService.CustomerStatistics> getCustomerStatistics() {
        CustomerService.CustomerStatistics statistics = customerService.getCustomerStatistics();
        return ResponseEntity.ok(statistics);
    }
}
