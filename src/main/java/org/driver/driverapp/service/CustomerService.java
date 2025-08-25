package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.customer.request.CreateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.request.UpdateCustomerRequestDTO;
import org.driver.driverapp.dto.customer.request.UpdateDeliveryPreferencesRequestDTO;
import org.driver.driverapp.dto.customer.response.CustomerResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.CustomerMapper;
import org.driver.driverapp.model.Customer;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.CustomerRepository;
import org.driver.driverapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;

    // Create customer
    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO request) {
        log.info("Creating customer: {}", request.getFullName());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Check if customer already exists for this user
        if (customerRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Customer already exists for user: " + request.getUserId());
        }

        // Check if phone already exists
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhone());
        }

        // Check if email already exists (if provided)
        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Create customer
        Customer customer = customerMapper.toEntity(request);
        customer.setUser(user);
        customer = customerRepository.save(customer);

        log.info("Created customer with id: {}", customer.getId());
        return customerMapper.toResponseDto(customer);
    }

    // Get customer by id
    public CustomerResponseDTO getCustomerById(Long id) {
        log.info("Getting customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        return customerMapper.toResponseDto(customer);
    }

    // Get customer by user id
    public CustomerResponseDTO getCustomerByUserId(Long userId) {
        log.info("Getting customer for user: {}", userId);

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + userId));

        return customerMapper.toResponseDto(customer);
    }

    // Get all customers with pagination
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        log.info("Getting all customers with pagination");

        Page<Customer> customers = customerRepository.findByActiveTrue(pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Get customers by region
    public Page<CustomerResponseDTO> getCustomersByRegion(String region, Pageable pageable) {
        log.info("Getting customers for region: {}", region);

        Page<Customer> customers = customerRepository.findByRegionAndActiveTrue(region, pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Get verified customers
    public Page<CustomerResponseDTO> getVerifiedCustomers(Pageable pageable) {
        log.info("Getting verified customers");

        Page<Customer> customers = customerRepository.findByActiveTrueAndVerifiedTrue(pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Search customers by name, phone, or email
    public Page<CustomerResponseDTO> searchCustomers(String searchTerm, Pageable pageable) {
        log.info("Searching customers by term: {}", searchTerm);

        Page<Customer> customers = customerRepository.searchByFullNameOrPhoneOrEmail(searchTerm, pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Search customers by name
    public Page<CustomerResponseDTO> searchCustomersByName(String name, Pageable pageable) {
        log.info("Searching customers by name: {}", name);

        Page<Customer> customers = customerRepository.findByFullNameContainingIgnoreCase(name, pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Search customers by phone
    public Page<CustomerResponseDTO> searchCustomersByPhone(String phone, Pageable pageable) {
        log.info("Searching customers by phone: {}", phone);

        Page<Customer> customers = customerRepository.findByPhoneContaining(phone, pageable);
        return customers.map(customerMapper::toResponseDto);
    }

    // Update customer
    public CustomerResponseDTO updateCustomer(Long id, UpdateCustomerRequestDTO request) {
        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Check if new phone conflicts with existing customer (excluding current)
        if (request.getPhone() != null && !request.getPhone().equals(customer.getPhone())) {
            Optional<Customer> existingByPhone = customerRepository.findByPhone(request.getPhone());
            if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(id)) {
                throw new IllegalArgumentException("Phone number already exists: " + request.getPhone());
            }
        }

        // Check if new email conflicts with existing customer (excluding current)
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(request.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
        }

        // Update customer fields
        customerMapper.updateEntityFromDto(request, customer);
        customer = customerRepository.save(customer);

        log.info("Updated customer with id: {}", id);
        return customerMapper.toResponseDto(customer);
    }

    // Delete customer (soft delete)
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setActive(false);
        customerRepository.save(customer);

        log.info("Deleted customer with id: {}", id);
    }

    // Verify customer
    public CustomerResponseDTO verifyCustomer(Long id) {
        log.info("Verifying customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.verify();
        customer = customerRepository.save(customer);

        log.info("Verified customer with id: {}", id);
        return customerMapper.toResponseDto(customer);
    }

    // Unverify customer
    public CustomerResponseDTO unverifyCustomer(Long id) {
        log.info("Unverifying customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.unverify();
        customer = customerRepository.save(customer);

        log.info("Unverified customer with id: {}", id);
        return customerMapper.toResponseDto(customer);
    }

    // Update delivery preferences
    public CustomerResponseDTO updateDeliveryPreferences(Long id, UpdateDeliveryPreferencesRequestDTO request) {
        log.info("Updating delivery preferences for customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setDeliveryPreferences(request.getDeliveryPreferences());
        customer = customerRepository.save(customer);

        log.info("Updated delivery preferences for customer: {}", id);
        return customerMapper.toResponseDto(customer);
    }



    // Get customer statistics
    public CustomerStatistics getCustomerStatistics() {
        log.info("Getting customer statistics");

        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByActiveTrue();
        long verifiedCustomers = customerRepository.countByVerifiedTrue();

        return CustomerStatistics.builder()
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .verifiedCustomers(verifiedCustomers)
                .build();
    }

    // Statistics DTO
    public static class CustomerStatistics {
        private long totalCustomers;
        private long activeCustomers;
        private long verifiedCustomers;

        // Builder pattern
        public static CustomerStatisticsBuilder builder() {
            return new CustomerStatisticsBuilder();
        }

        public static class CustomerStatisticsBuilder {
            private long totalCustomers;
            private long activeCustomers;
            private long verifiedCustomers;

            public CustomerStatisticsBuilder totalCustomers(long totalCustomers) {
                this.totalCustomers = totalCustomers;
                return this;
            }

            public CustomerStatisticsBuilder activeCustomers(long activeCustomers) {
                this.activeCustomers = activeCustomers;
                return this;
            }

            public CustomerStatisticsBuilder verifiedCustomers(long verifiedCustomers) {
                this.verifiedCustomers = verifiedCustomers;
                return this;
            }

            public CustomerStatistics build() {
                CustomerStatistics statistics = new CustomerStatistics();
                statistics.totalCustomers = this.totalCustomers;
                statistics.activeCustomers = this.activeCustomers;
                statistics.verifiedCustomers = this.verifiedCustomers;
                return statistics;
            }
        }

        // Getters
        public long getTotalCustomers() { return totalCustomers; }
        public long getActiveCustomers() { return activeCustomers; }
        public long getVerifiedCustomers() { return verifiedCustomers; }
    }
}
