package org.driver.driverapp.service;

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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private User testUser;
    private Customer testCustomer;
    private CreateCustomerRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@email.com")
                .enabled(true)
                .build();

        // Setup test customer
        testCustomer = Customer.builder()
                .id(1L)
                .user(testUser)
                .fullName("Test Customer")
                .phone("+251911234567")
                .email("test@customer.com")
                .preferredPayment("CASH")
                .region("Addis Ababa")
                .active(true)
                .verified(false)
                .build();

        // Setup create request
        createRequest = CreateCustomerRequestDTO.builder()
                .userId(1L)
                .fullName("Test Customer")
                .phone("+251911234567")
                .email("test@customer.com")
                .preferredPayment("CASH")
                .region("Addis Ababa")
                .build();
    }

    // Customer CRUD Tests

    @Test
    void createCustomer_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(customerRepository.existsByPhone("+251911234567")).thenReturn(false);
        when(customerRepository.existsByEmail("test@customer.com")).thenReturn(false);
        when(customerMapper.toEntity(createRequest)).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.createCustomer(createRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(customerRepository).findByUserId(1L);
        verify(customerRepository).existsByPhone("+251911234567");
        verify(customerRepository).existsByEmail("test@customer.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.createCustomer(createRequest);
        });
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_CustomerAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(testCustomer));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(createRequest);
        });
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_PhoneAlreadyExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(customerRepository.existsByPhone("+251911234567")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(createRequest);
        });
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getCustomerById_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.getCustomerById(1L);

        // Then
        assertNotNull(result);
        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_NotFound() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });
    }

    @Test
    void getCustomerByUserId_Success() {
        // Given
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.getCustomerByUserId(1L);

        // Then
        assertNotNull(result);
        verify(customerRepository).findByUserId(1L);
    }

    @Test
    void getAllCustomers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepository.findByActiveTrue(pageable)).thenReturn(page);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        Page<CustomerResponseDTO> result = customerService.getAllCustomers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).findByActiveTrue(pageable);
    }

    @Test
    void updateCustomer_Success() {
        // Given
        UpdateCustomerRequestDTO updateRequest = UpdateCustomerRequestDTO.builder()
                .fullName("Updated Customer")
                .phone("+251922345678")
                .email("updated@customer.com")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findByPhone("+251922345678")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("updated@customer.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.updateCustomer(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        customerService.deleteCustomer(1L);

        // Then
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
        assertFalse(testCustomer.getActive());
    }

    @Test
    void verifyCustomer_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.verifyCustomer(1L);

        // Then
        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
        assertTrue(testCustomer.getVerified());
    }

    @Test
    void unverifyCustomer_Success() {
        // Given
        testCustomer.setVerified(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.unverifyCustomer(1L);

        // Then
        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
        assertFalse(testCustomer.getVerified());
    }

    @Test
    void updateDeliveryPreferences_Success() {
        // Given
        UpdateDeliveryPreferencesRequestDTO request = UpdateDeliveryPreferencesRequestDTO.builder()
                .deliveryPreferences("{\"preferredTime\": \"morning\"}")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        CustomerResponseDTO result = customerService.updateDeliveryPreferences(1L, request);

        // Then
        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
        assertEquals("{\"preferredTime\": \"morning\"}", testCustomer.getDeliveryPreferences());
    }







    @Test
    void getCustomerStatistics_Success() {
        // Given
        when(customerRepository.count()).thenReturn(10L);
        when(customerRepository.countByActiveTrue()).thenReturn(8L);
        when(customerRepository.countByVerifiedTrue()).thenReturn(5L);

        // When
        CustomerService.CustomerStatistics result = customerService.getCustomerStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalCustomers());
        assertEquals(8L, result.getActiveCustomers());
        assertEquals(5L, result.getVerifiedCustomers());
    }

    // Search Tests

    @Test
    void searchCustomers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepository.searchByFullNameOrPhoneOrEmail("test", pageable)).thenReturn(page);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        Page<CustomerResponseDTO> result = customerService.searchCustomers("test", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).searchByFullNameOrPhoneOrEmail("test", pageable);
    }

    @Test
    void getCustomersByRegion_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepository.findByRegionAndActiveTrue("Addis Ababa", pageable)).thenReturn(page);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        Page<CustomerResponseDTO> result = customerService.getCustomersByRegion("Addis Ababa", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).findByRegionAndActiveTrue("Addis Ababa", pageable);
    }

    @Test
    void getVerifiedCustomers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepository.findByActiveTrueAndVerifiedTrue(pageable)).thenReturn(page);
        when(customerMapper.toResponseDto(testCustomer)).thenReturn(new CustomerResponseDTO());

        // When
        Page<CustomerResponseDTO> result = customerService.getVerifiedCustomers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository).findByActiveTrueAndVerifiedTrue(pageable);
    }
}
