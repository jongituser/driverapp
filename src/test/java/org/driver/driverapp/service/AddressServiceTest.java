package org.driver.driverapp.service;

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
import org.driver.driverapp.model.PostalCode;
import org.driver.driverapp.repository.AddressRepository;
import org.driver.driverapp.repository.CustomerRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.repository.PostalCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PostalCodeRepository postalCodeRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Customer testCustomer;
    private PostalCode testPostalCode;
    private Address testAddress;
    private AddressResponseDTO testAddressResponseDTO;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .fullName("Test Customer")
                .email("test@example.com")
                .phone("+251912345678")
                .active(true)
                .build();

        testPostalCode = PostalCode.builder()
                .id(1L)
                .region(EthiopianRegion.ADDIS_ABABA)
                .code("1000")
                .description("Addis Ababa Central")
                .active(true)
                .build();

        testAddress = Address.builder()
                .id(1L)
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                .kebele("Kebele 01")
                .description("Test address")
                .customer(testCustomer)
                .postalCode(testPostalCode)
                .active(true)
                .build();

        testAddressResponseDTO = AddressResponseDTO.builder()
                .id(1L)
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                .kebele("Kebele 01")
                .description("Test address")
                .customerId(1L)
                .postalCode("1000")
                .addressType("HYBRID")
                .active(true)
                .build();
    }

    @Test
    void createAddress_Success() {
        // Arrange
        CreateAddressRequestDTO requestDTO = CreateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                .kebele("Kebele 01")
                .description("Test address")
                .customerId(1L)
                .postalCodeId(1L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(postalCodeRepository.findById(1L)).thenReturn(Optional.of(testPostalCode));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(addressMapper.toResponseDTO(testAddress)).thenReturn(testAddressResponseDTO);

        // Act
        AddressResponseDTO result = addressService.createAddress(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testAddressResponseDTO.getId(), result.getId());
        assertEquals(testAddressResponseDTO.getAddressType(), result.getAddressType());

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void createAddress_GpsOnly_Success() {
        // Arrange
        CreateAddressRequestDTO requestDTO = CreateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .description("GPS only address")
                .customerId(1L)
                .build();

        Address gpsOnlyAddress = Address.builder()
                .id(2L)
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .description("GPS only address")
                .customer(testCustomer)
                .active(true)
                .build();

        AddressResponseDTO gpsOnlyResponseDTO = AddressResponseDTO.builder()
                .id(2L)
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .description("GPS only address")
                .customerId(1L)
                .addressType("GPS_ONLY")
                .active(true)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.save(any(Address.class))).thenReturn(gpsOnlyAddress);
        when(addressMapper.toResponseDTO(gpsOnlyAddress)).thenReturn(gpsOnlyResponseDTO);

        // Act
        AddressResponseDTO result = addressService.createAddress(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("GPS_ONLY", result.getAddressType());
        assertNull(result.getRegion());
        assertNull(result.getWoreda());
        assertNull(result.getKebele());

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void createAddress_InvalidAddress_ThrowsException() {
        // Arrange
        CreateAddressRequestDTO requestDTO = CreateAddressRequestDTO.builder()
                .description("Invalid address - no GPS or Ethiopian address")
                .customerId(1L)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                addressService.createAddress(requestDTO));

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void createAddress_CustomerNotFound_ThrowsException() {
        // Arrange
        CreateAddressRequestDTO requestDTO = CreateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .customerId(999L)
                .build();

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                addressService.createAddress(requestDTO));

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateAddress_Success() {
        // Arrange
        Long addressId = 1L;
        UpdateAddressRequestDTO requestDTO = UpdateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Bole")
                .kebele("Kebele 03")
                .description("Updated address")
                .postalCodeId(1L)
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(postalCodeRepository.findById(1L)).thenReturn(Optional.of(testPostalCode));
        when(addressMapper.updateEntityFromDto(requestDTO, testAddress)).thenReturn(testAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(addressMapper.toResponseDTO(testAddress)).thenReturn(testAddressResponseDTO);

        // Act
        AddressResponseDTO result = addressService.updateAddress(addressId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testAddressResponseDTO.getId(), result.getId());

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void updateAddress_AddressNotFound_ThrowsException() {
        // Arrange
        Long addressId = 999L;
        UpdateAddressRequestDTO requestDTO = UpdateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                addressService.updateAddress(addressId, requestDTO));

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_Success() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository).save(any(Address.class));
        assertFalse(testAddress.isActive());
    }

    @Test
    void getAddressById_Success() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(addressMapper.toResponseDTO(testAddress)).thenReturn(testAddressResponseDTO);

        // Act
        AddressResponseDTO result = addressService.getAddressById(addressId);

        // Assert
        assertNotNull(result);
        assertEquals(testAddressResponseDTO.getId(), result.getId());
    }

    @Test
    void getAddressById_NotFound_ThrowsException() {
        // Arrange
        Long addressId = 999L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                addressService.getAddressById(addressId));
    }

    @Test
    void getAddressesByCustomer_Success() {
        // Arrange
        Long customerId = 1L;
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findByCustomerIdAndActiveTrue(customerId)).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getAddressesByCustomer(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAddressesByCustomer_WithPagination_Success() {
        // Arrange
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Address> addressPage = new PageImpl<>(List.of(testAddress));

        when(addressRepository.findByCustomerIdAndActiveTrue(customerId, pageable)).thenReturn(addressPage);
        when(addressMapper.toResponseDTO(testAddress)).thenReturn(testAddressResponseDTO);

        // Act
        Page<AddressResponseDTO> result = addressService.getAddressesByCustomer(customerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testAddressResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void validateAddress_GpsOnly_Valid() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertTrue(result.isValid());
        assertEquals("GPS_ONLY", result.getAddressType());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validateAddress_FullEthiopian_Valid() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                .kebele("Kebele 01")
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertTrue(result.isValid());
        assertEquals("FULL_ETHIOPIAN", result.getAddressType());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validateAddress_Hybrid_Valid() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(9.0320))
                .gpsLong(BigDecimal.valueOf(38.7489))
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                .kebele("Kebele 01")
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertTrue(result.isValid());
        assertEquals("HYBRID", result.getAddressType());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validateAddress_Invalid_NoGpsOrEthiopian() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertFalse(result.isValid());
        assertEquals("INVALID", result.getAddressType());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("At least GPS coordinates OR (region + woreda + kebele) must be provided"));
    }

    @Test
    void validateAddress_InvalidGpsCoordinates() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .gpsLat(BigDecimal.valueOf(100.0)) // Invalid latitude
                .gpsLong(BigDecimal.valueOf(38.7489))
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Latitude must be between -90 and 90 degrees"));
    }

    @Test
    void validateAddress_PartialEthiopianAddress_Invalid() {
        // Arrange
        ValidateAddressRequestDTO requestDTO = ValidateAddressRequestDTO.builder()
                .region(EthiopianRegion.ADDIS_ABABA)
                .woreda("Kolfe Keranio")
                // Missing kebele
                .build();

        // Act
        AddressValidationResponseDTO result = addressService.validateAddress(requestDTO);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Kebele is required when providing Ethiopian address"));
    }

    @Test
    void getAddressesByRegion_Success() {
        // Arrange
        EthiopianRegion region = EthiopianRegion.ADDIS_ABABA;
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findByRegionAndActiveTrue(region)).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getAddressesByRegion(region);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAddressesByWoreda_Success() {
        // Arrange
        String woreda = "Kolfe Keranio";
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findByWoredaAndActiveTrue(woreda)).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getAddressesByWoreda(woreda);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAddressesByKebele_Success() {
        // Arrange
        String kebele = "Kebele 01";
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findByKebeleAndActiveTrue(kebele)).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getAddressesByKebele(kebele);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAddressesWithinRadius_Success() {
        // Arrange
        BigDecimal lat = BigDecimal.valueOf(9.0320);
        BigDecimal lng = BigDecimal.valueOf(38.7489);
        BigDecimal radius = BigDecimal.valueOf(0.01);
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findAddressesWithinRadius(lat, lng, radius)).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getAddressesWithinRadius(lat, lng, radius);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getGpsOnlyAddresses_Success() {
        // Arrange
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findGpsOnlyAddresses()).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getGpsOnlyAddresses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getFullEthiopianAddresses_Success() {
        // Arrange
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findFullEthiopianAddresses()).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getFullEthiopianAddresses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getHybridAddresses_Success() {
        // Arrange
        List<Address> addresses = List.of(testAddress);
        List<AddressResponseDTO> addressResponseDTOs = List.of(testAddressResponseDTO);

        when(addressRepository.findHybridAddresses()).thenReturn(addresses);
        when(addressMapper.toResponseDTOList(addresses)).thenReturn(addressResponseDTOs);

        // Act
        List<AddressResponseDTO> result = addressService.getHybridAddresses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddressResponseDTO.getId(), result.get(0).getId());
    }
}
