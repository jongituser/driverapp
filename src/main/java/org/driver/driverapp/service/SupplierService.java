package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.inventory.request.CreateSupplierRequestDTO;
import org.driver.driverapp.dto.inventory.response.SupplierResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.SupplierMapper;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.Supplier;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final PartnerRepository partnerRepository;
    private final SupplierMapper supplierMapper;

    // Create supplier
    public SupplierResponseDTO createSupplier(CreateSupplierRequestDTO request) {
        log.info("Creating supplier: {}", request.getName());

        // Validate partner exists
        partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + request.getPartnerId()));

        // Check if supplier name already exists
        if (supplierRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Supplier name already exists: " + request.getName());
        }

        // Check if phone already exists
        if (supplierRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhone());
        }

        // Check if email already exists (if provided)
        if (request.getEmail() != null && supplierRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Create supplier
        Supplier supplier = supplierMapper.toEntity(request);
        supplier = supplierRepository.save(supplier);

        log.info("Created supplier with id: {}", supplier.getId());
        return supplierMapper.toResponseDto(supplier);
    }

    // Get supplier by id
    public SupplierResponseDTO getSupplierById(Long id) {
        log.info("Getting supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        return supplierMapper.toResponseDto(supplier);
    }

    // Get all suppliers with pagination
    public Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable) {
        log.info("Getting all suppliers with pagination");

        Page<Supplier> suppliers = supplierRepository.findByActiveTrue(pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Get suppliers by partner
    public Page<SupplierResponseDTO> getSuppliersByPartner(Long partnerId, Pageable pageable) {
        log.info("Getting suppliers for partner: {}", partnerId);

        Page<Supplier> suppliers = supplierRepository.findByPartnerIdAndActiveTrue(partnerId, pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Get suppliers by region
    public Page<SupplierResponseDTO> getSuppliersByRegion(String region, Pageable pageable) {
        log.info("Getting suppliers for region: {}", region);

        Page<Supplier> suppliers = supplierRepository.findByRegion(region, pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Get suppliers by city
    public Page<SupplierResponseDTO> getSuppliersByCity(String city, Pageable pageable) {
        log.info("Getting suppliers for city: {}", city);

        Page<Supplier> suppliers = supplierRepository.findByCity(city, pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Get verified suppliers
    public Page<SupplierResponseDTO> getVerifiedSuppliers(Pageable pageable) {
        log.info("Getting verified suppliers");

        Page<Supplier> suppliers = supplierRepository.findByVerifiedTrue(pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Search suppliers by name
    public Page<SupplierResponseDTO> searchSuppliersByName(String searchTerm, Pageable pageable) {
        log.info("Searching suppliers by name: {}", searchTerm);

        Page<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        return suppliers.map(supplierMapper::toResponseDto);
    }

    // Update supplier
    public SupplierResponseDTO updateSupplier(Long id, CreateSupplierRequestDTO request) {
        log.info("Updating supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        // Check if new name conflicts with existing supplier (excluding current)
        Optional<Supplier> existingByName = supplierRepository.findByNameIgnoreCase(request.getName());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Supplier name already exists: " + request.getName());
        }

        // Check if new phone conflicts with existing supplier (excluding current)
        Optional<Supplier> existingByPhone = supplierRepository.findByPhone(request.getPhone());
        if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(id)) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhone());
        }

        // Check if new email conflicts with existing supplier (excluding current)
        if (request.getEmail() != null) {
            Optional<Supplier> existingByEmail = supplierRepository.findByEmail(request.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
        }

        // Update supplier fields
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setRegion(request.getRegion());

        // Update partner if changed
        if (!supplier.getPartner().getId().equals(request.getPartnerId())) {
            Partner newPartner = partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + request.getPartnerId()));
            supplier.setPartner(newPartner);
        }

        supplier = supplierRepository.save(supplier);

        log.info("Updated supplier with id: {}", id);
        return supplierMapper.toResponseDto(supplier);
    }

    // Delete supplier
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setActive(false);
        supplierRepository.save(supplier);

        log.info("Deleted supplier with id: {}", id);
    }

    // Verify supplier
    public SupplierResponseDTO verifySupplier(Long id) {
        log.info("Verifying supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setVerified(true);
        supplier = supplierRepository.save(supplier);

        log.info("Verified supplier with id: {}", id);
        return supplierMapper.toResponseDto(supplier);
    }

    // Unverify supplier
    public SupplierResponseDTO unverifySupplier(Long id) {
        log.info("Unverifying supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setVerified(false);
        supplier = supplierRepository.save(supplier);

        log.info("Unverified supplier with id: {}", id);
        return supplierMapper.toResponseDto(supplier);
    }

    // Get supplier statistics
    public SupplierStatistics getSupplierStatistics(Long partnerId) {
        log.info("Getting supplier statistics for partner: {}", partnerId);

        long totalSuppliers = supplierRepository.countByPartnerId(partnerId);
        long activeSuppliers = supplierRepository.countByActiveTrue();
        long verifiedSuppliers = supplierRepository.countByVerifiedTrue();

        return SupplierStatistics.builder()
                .totalSuppliers(totalSuppliers)
                .activeSuppliers(activeSuppliers)
                .verifiedSuppliers(verifiedSuppliers)
                .build();
    }

    // Statistics DTO
    public static class SupplierStatistics {
        private long totalSuppliers;
        private long activeSuppliers;
        private long verifiedSuppliers;

        // Builder pattern
        public static SupplierStatisticsBuilder builder() {
            return new SupplierStatisticsBuilder();
        }

        public static class SupplierStatisticsBuilder {
            private long totalSuppliers;
            private long activeSuppliers;
            private long verifiedSuppliers;

            public SupplierStatisticsBuilder totalSuppliers(long totalSuppliers) {
                this.totalSuppliers = totalSuppliers;
                return this;
            }

            public SupplierStatisticsBuilder activeSuppliers(long activeSuppliers) {
                this.activeSuppliers = activeSuppliers;
                return this;
            }

            public SupplierStatisticsBuilder verifiedSuppliers(long verifiedSuppliers) {
                this.verifiedSuppliers = verifiedSuppliers;
                return this;
            }

            public SupplierStatistics build() {
                SupplierStatistics statistics = new SupplierStatistics();
                statistics.totalSuppliers = this.totalSuppliers;
                statistics.activeSuppliers = this.activeSuppliers;
                statistics.verifiedSuppliers = this.verifiedSuppliers;
                return statistics;
            }
        }

        // Getters
        public long getTotalSuppliers() { return totalSuppliers; }
        public long getActiveSuppliers() { return activeSuppliers; }
        public long getVerifiedSuppliers() { return verifiedSuppliers; }
    }
}
