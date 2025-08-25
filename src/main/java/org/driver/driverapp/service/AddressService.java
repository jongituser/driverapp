package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.address.request.CreateAddressRequestDTO;
import org.driver.driverapp.dto.address.request.UpdateAddressRequestDTO;
import org.driver.driverapp.dto.address.request.ValidateAddressRequestDTO;
import org.driver.driverapp.dto.address.response.AddressResponseDTO;
import org.driver.driverapp.dto.address.response.AddressValidationResponseDTO;
import org.driver.driverapp.enums.EthiopianRegion;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.AddressMapper;
import org.driver.driverapp.model.Address;
import org.driver.driverapp.model.Customer;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.PostalCode;
import org.driver.driverapp.repository.AddressRepository;
import org.driver.driverapp.repository.CustomerRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.repository.PostalCodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final PartnerRepository partnerRepository;
    private final PostalCodeRepository postalCodeRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public AddressResponseDTO createAddress(CreateAddressRequestDTO requestDTO) {
        log.info("Creating address for customer: {}, partner: {}", requestDTO.getCustomerId(), requestDTO.getPartnerId());

        // Validate address
        AddressValidationResponseDTO validation = validateAddress(requestDTO);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Invalid address: " + validation.getValidationMessage());
        }

        // Build address
        Address address = Address.builder()
                .gpsLat(requestDTO.getGpsLat())
                .gpsLong(requestDTO.getGpsLong())
                .region(requestDTO.getRegion())
                .woreda(requestDTO.getWoreda())
                .kebele(requestDTO.getKebele())
                .description(requestDTO.getDescription())
                .active(true)
                .build();

        // Set postal code if provided
        if (requestDTO.getPostalCodeId() != null) {
            PostalCode postalCode = postalCodeRepository.findById(requestDTO.getPostalCodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Postal code not found with id: " + requestDTO.getPostalCodeId()));
            address.setPostalCode(postalCode);
        }

        // Set owner (customer or partner)
        if (requestDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + requestDTO.getCustomerId()));
            address.setCustomer(customer);
        } else if (requestDTO.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(requestDTO.getPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + requestDTO.getPartnerId()));
            address.setPartner(partner);
        }

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully: {}", savedAddress.getId());

        return addressMapper.toResponseDTO(savedAddress);
    }

    @Transactional
    public AddressResponseDTO updateAddress(Long id, UpdateAddressRequestDTO requestDTO) {
        log.info("Updating address: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        // Validate updated address
        AddressValidationResponseDTO validation = validateAddress(requestDTO);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Invalid address: " + validation.getValidationMessage());
        }

        // Update postal code if provided
        if (requestDTO.getPostalCodeId() != null) {
            PostalCode postalCode = postalCodeRepository.findById(requestDTO.getPostalCodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Postal code not found with id: " + requestDTO.getPostalCodeId()));
            address.setPostalCode(postalCode);
        } else {
            address.setPostalCode(null);
        }

        // Update fields using mapper
        address = addressMapper.updateEntityFromDto(requestDTO, address);
        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully: {}", updatedAddress.getId());

        return addressMapper.toResponseDTO(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long id) {
        log.info("Deleting address: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        address.setActive(false);
        addressRepository.save(address);
        log.info("Address deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public AddressResponseDTO getAddressById(Long id) {
        log.info("Getting address by id: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        return addressMapper.toResponseDTO(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByCustomer(Long customerId) {
        log.info("Getting addresses for customer: {}", customerId);

        List<Address> addresses = addressRepository.findByCustomerIdAndActiveTrue(customerId);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByPartner(Long partnerId) {
        log.info("Getting addresses for partner: {}", partnerId);

        List<Address> addresses = addressRepository.findByPartnerIdAndActiveTrue(partnerId);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public Page<AddressResponseDTO> getAddressesByCustomer(Long customerId, Pageable pageable) {
        log.info("Getting addresses for customer: {} with pagination", customerId);

        Page<Address> addresses = addressRepository.findByCustomerIdAndActiveTrue(customerId, pageable);
        return addresses.map(addressMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AddressResponseDTO> getAddressesByPartner(Long partnerId, Pageable pageable) {
        log.info("Getting addresses for partner: {} with pagination", partnerId);

        Page<Address> addresses = addressRepository.findByPartnerIdAndActiveTrue(partnerId, pageable);
        return addresses.map(addressMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByRegion(EthiopianRegion region) {
        log.info("Getting addresses by region: {}", region);

        List<Address> addresses = addressRepository.findByRegionAndActiveTrue(region);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByWoreda(String woreda) {
        log.info("Getting addresses by woreda: {}", woreda);

        List<Address> addresses = addressRepository.findByWoredaAndActiveTrue(woreda);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesByKebele(String kebele) {
        log.info("Getting addresses by kebele: {}", kebele);

        List<Address> addresses = addressRepository.findByKebeleAndActiveTrue(kebele);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getAddressesWithinRadius(BigDecimal lat, BigDecimal lng, BigDecimal radius) {
        log.info("Getting addresses within radius: {} from ({}, {})", radius, lat, lng);

        List<Address> addresses = addressRepository.findAddressesWithinRadius(lat, lng, radius);
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public AddressValidationResponseDTO validateAddress(ValidateAddressRequestDTO requestDTO) {
        log.info("Validating address");

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        boolean isValid = true;

        // Check if at least GPS OR Ethiopian address is provided
        boolean hasGps = requestDTO.getGpsLat() != null && requestDTO.getGpsLong() != null;
        boolean hasEthiopianAddress = requestDTO.getRegion() != null && 
                                    requestDTO.getWoreda() != null && 
                                    requestDTO.getKebele() != null;

        // Check for partial Ethiopian address (some components but not all)
        boolean hasPartialEthiopianAddress = (requestDTO.getRegion() != null || 
                                             requestDTO.getWoreda() != null || 
                                             requestDTO.getKebele() != null) && !hasEthiopianAddress;

        // Validate GPS coordinates if provided
        if (hasGps) {
            if (requestDTO.getGpsLat().compareTo(BigDecimal.valueOf(-90)) < 0 || 
                requestDTO.getGpsLat().compareTo(BigDecimal.valueOf(90)) > 0) {
                errors.add("Latitude must be between -90 and 90 degrees");
                isValid = false;
            }
            if (requestDTO.getGpsLong().compareTo(BigDecimal.valueOf(-180)) < 0 || 
                requestDTO.getGpsLong().compareTo(BigDecimal.valueOf(180)) > 0) {
                errors.add("Longitude must be between -180 and 180 degrees");
                isValid = false;
            }
        }

        // Validate Ethiopian address components if any are provided
        if (hasPartialEthiopianAddress || hasEthiopianAddress) {
            if (requestDTO.getRegion() == null) {
                errors.add("Region is required when providing Ethiopian address");
                isValid = false;
            }
            if (requestDTO.getWoreda() == null || requestDTO.getWoreda().trim().isEmpty()) {
                errors.add("Woreda is required when providing Ethiopian address");
                isValid = false;
            }
            if (requestDTO.getKebele() == null || requestDTO.getKebele().trim().isEmpty()) {
                errors.add("Kebele is required when providing Ethiopian address");
                isValid = false;
            }
        }

        // Check if at least GPS OR complete Ethiopian address is provided
        if (!hasGps && !hasEthiopianAddress) {
            errors.add("At least GPS coordinates OR (region + woreda + kebele) must be provided");
            isValid = false;
        }

        // Validate postal code if provided
        if (requestDTO.getPostalCode() != null && !requestDTO.getPostalCode().trim().isEmpty()) {
            boolean postalCodeExists = postalCodeRepository.existsByCodeAndActiveTrue(requestDTO.getPostalCode());
            if (!postalCodeExists) {
                warnings.add("Postal code not found in database");
            }
        }

        // Determine address type
        String addressType;
        if (!isValid) {
            addressType = "INVALID";
        } else if (hasGps && !hasEthiopianAddress) {
            addressType = "GPS_ONLY";
        } else if (hasGps && hasEthiopianAddress) {
            addressType = "HYBRID";
        } else {
            addressType = "FULL_ETHIOPIAN";
        }

        String validationMessage = isValid ? "Address is valid" : "Address validation failed";

        return AddressValidationResponseDTO.builder()
                .isValid(isValid)
                .validationMessage(validationMessage)
                .errors(errors)
                .warnings(warnings)
                .addressType(addressType)
                .build();
    }

    @Transactional(readOnly = true)
    public AddressValidationResponseDTO validateAddress(CreateAddressRequestDTO requestDTO) {
        ValidateAddressRequestDTO validateRequest = ValidateAddressRequestDTO.builder()
                .gpsLat(requestDTO.getGpsLat())
                .gpsLong(requestDTO.getGpsLong())
                .region(requestDTO.getRegion())
                .woreda(requestDTO.getWoreda())
                .kebele(requestDTO.getKebele())
                .postalCode(requestDTO.getPostalCodeId() != null ? 
                    postalCodeRepository.findById(requestDTO.getPostalCodeId()).map(PostalCode::getCode).orElse(null) : null)
                .build();

        return validateAddress(validateRequest);
    }

    @Transactional(readOnly = true)
    public AddressValidationResponseDTO validateAddress(UpdateAddressRequestDTO requestDTO) {
        ValidateAddressRequestDTO validateRequest = ValidateAddressRequestDTO.builder()
                .gpsLat(requestDTO.getGpsLat())
                .gpsLong(requestDTO.getGpsLong())
                .region(requestDTO.getRegion())
                .woreda(requestDTO.getWoreda())
                .kebele(requestDTO.getKebele())
                .postalCode(requestDTO.getPostalCodeId() != null ? 
                    postalCodeRepository.findById(requestDTO.getPostalCodeId()).map(PostalCode::getCode).orElse(null) : null)
                .build();

        return validateAddress(validateRequest);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getGpsOnlyAddresses() {
        log.info("Getting GPS-only addresses");

        List<Address> addresses = addressRepository.findGpsOnlyAddresses();
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getFullEthiopianAddresses() {
        log.info("Getting full Ethiopian addresses");

        List<Address> addresses = addressRepository.findFullEthiopianAddresses();
        return addressMapper.toResponseDTOList(addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getHybridAddresses() {
        log.info("Getting hybrid addresses");

        List<Address> addresses = addressRepository.findHybridAddresses();
        return addressMapper.toResponseDTOList(addresses);
    }
}
