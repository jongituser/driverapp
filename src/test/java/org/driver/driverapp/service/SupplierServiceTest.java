package org.driver.driverapp.service;

import org.driver.driverapp.dto.inventory.request.CreateSupplierRequestDTO;
import org.driver.driverapp.dto.inventory.response.SupplierResponseDTO;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.SupplierMapper;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.model.Supplier;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.repository.SupplierRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Partner testPartner;
    private Supplier testSupplier;
    private CreateSupplierRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        // Setup test partner
        testPartner = Partner.builder()
                .id(1L)
                .name("Test Partner")
                .phone("+251911234567")
                .email("test@partner.com")
                .active(true)
                .build();

        // Setup test supplier
        testSupplier = Supplier.builder()
                .id(1L)
                .name("Test Supplier")
                .phone("+251922345678")
                .email("test@supplier.com")
                .address("Test Address")
                .city("Addis Ababa")
                .region("Addis Ababa")
                .active(true)
                .verified(false)
                .partner(testPartner)
                .build();

        // Setup create request
        createRequest = CreateSupplierRequestDTO.builder()
                .name("Test Supplier")
                .phone("+251922345678")
                .email("test@supplier.com")
                .address("Test Address")
                .city("Addis Ababa")
                .region("Addis Ababa")
                .partnerId(1L)
                .build();
    }

    @Test
    void createSupplier_Success() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(supplierRepository.existsByNameIgnoreCase("Test Supplier")).thenReturn(false);
        when(supplierRepository.existsByPhone("+251922345678")).thenReturn(false);
        when(supplierRepository.existsByEmail("test@supplier.com")).thenReturn(false);
        when(supplierMapper.toEntity(createRequest)).thenReturn(testSupplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        SupplierResponseDTO result = supplierService.createSupplier(createRequest);

        // Then
        assertNotNull(result);
        verify(partnerRepository).findById(1L);
        verify(supplierRepository).existsByNameIgnoreCase("Test Supplier");
        verify(supplierRepository).existsByPhone("+251922345678");
        verify(supplierRepository).existsByEmail("test@supplier.com");
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void createSupplier_PartnerNotFound() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.createSupplier(createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void createSupplier_NameAlreadyExists() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(supplierRepository.existsByNameIgnoreCase("Test Supplier")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.createSupplier(createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void createSupplier_PhoneAlreadyExists() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(supplierRepository.existsByNameIgnoreCase("Test Supplier")).thenReturn(false);
        when(supplierRepository.existsByPhone("+251922345678")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.createSupplier(createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void createSupplier_EmailAlreadyExists() {
        // Given
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(supplierRepository.existsByNameIgnoreCase("Test Supplier")).thenReturn(false);
        when(supplierRepository.existsByPhone("+251922345678")).thenReturn(false);
        when(supplierRepository.existsByEmail("test@supplier.com")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.createSupplier(createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void getSupplierById_Success() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        SupplierResponseDTO result = supplierService.getSupplierById(1L);

        // Then
        assertNotNull(result);
        verify(supplierRepository).findById(1L);
    }

    @Test
    void getSupplierById_NotFound() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.getSupplierById(1L);
        });
    }

    @Test
    void getAllSuppliers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByActiveTrue(pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.getAllSuppliers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByActiveTrue(pageable);
    }

    @Test
    void getSuppliersByPartner_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByPartnerIdAndActiveTrue(1L, pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.getSuppliersByPartner(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByPartnerIdAndActiveTrue(1L, pageable);
    }

    @Test
    void getSuppliersByRegion_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByRegion("Addis Ababa", pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.getSuppliersByRegion("Addis Ababa", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByRegion("Addis Ababa", pageable);
    }

    @Test
    void getSuppliersByCity_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByCity("Addis Ababa", pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.getSuppliersByCity("Addis Ababa", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByCity("Addis Ababa", pageable);
    }

    @Test
    void getVerifiedSuppliers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByVerifiedTrue(pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.getVerifiedSuppliers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByVerifiedTrue(pageable);
    }

    @Test
    void searchSuppliersByName_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Supplier> page = new PageImpl<>(List.of(testSupplier));
        when(supplierRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(page);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        Page<SupplierResponseDTO> result = supplierService.searchSuppliersByName("Test", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplierRepository).findByNameContainingIgnoreCase("Test", pageable);
    }

    @Test
    void updateSupplier_Success() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.findByNameIgnoreCase("Updated Supplier")).thenReturn(Optional.empty());
        when(supplierRepository.findByPhone("+251933456789")).thenReturn(Optional.empty());
        when(supplierRepository.findByEmail("updated@supplier.com")).thenReturn(Optional.empty());
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        CreateSupplierRequestDTO updateRequest = CreateSupplierRequestDTO.builder()
                .name("Updated Supplier")
                .phone("+251933456789")
                .email("updated@supplier.com")
                .address("Updated Address")
                .city("Dire Dawa")
                .region("Dire Dawa")
                .partnerId(1L)
                .build();

        // When
        SupplierResponseDTO result = supplierService.updateSupplier(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void updateSupplier_SupplierNotFound() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.updateSupplier(1L, createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_NameConflict() {
        // Given
        Supplier existingSupplier = Supplier.builder().id(2L).name("Existing Supplier").build();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.findByNameIgnoreCase("Test Supplier")).thenReturn(Optional.of(existingSupplier));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.updateSupplier(1L, createRequest);
        });
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteSupplier_Success() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        // When
        supplierService.deleteSupplier(1L);

        // Then
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
        assertFalse(testSupplier.isActive());
    }

    @Test
    void verifySupplier_Success() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        SupplierResponseDTO result = supplierService.verifySupplier(1L);

        // Then
        assertNotNull(result);
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
        assertTrue(testSupplier.isVerified());
    }

    @Test
    void unverifySupplier_Success() {
        // Given
        testSupplier.setVerified(true);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);
        when(supplierMapper.toResponseDto(testSupplier)).thenReturn(new SupplierResponseDTO());

        // When
        SupplierResponseDTO result = supplierService.unverifySupplier(1L);

        // Then
        assertNotNull(result);
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(any(Supplier.class));
        assertFalse(testSupplier.isVerified());
    }

    @Test
    void getSupplierStatistics_Success() {
        // Given
        when(supplierRepository.countByPartnerId(1L)).thenReturn(5L);
        when(supplierRepository.countByActiveTrue()).thenReturn(10L);
        when(supplierRepository.countByVerifiedTrue()).thenReturn(3L);

        // When
        SupplierService.SupplierStatistics result = supplierService.getSupplierStatistics(1L);

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getTotalSuppliers());
        assertEquals(10L, result.getActiveSuppliers());
        assertEquals(3L, result.getVerifiedSuppliers());
    }
}
