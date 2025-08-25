package org.driver.driverapp.service;

import org.driver.driverapp.dto.payment.response.PaymentResponseDTO;
import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.PaymentMapper;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Payment;
import org.driver.driverapp.model.User;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.PaymentRepository;
import org.driver.driverapp.repository.UserRepository;
import org.driver.driverapp.service.payment.PaymentProviderFactory;
import org.driver.driverapp.service.payment.PaymentProviderService;
import org.driver.driverapp.service.payment.PaymentRequest;
import org.driver.driverapp.service.payment.PaymentResponse;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PaymentProviderFactory paymentProviderFactory;

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private Delivery testDelivery;
    private Payment testPayment;
    private PaymentResponseDTO testPaymentResponseDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .build();

        testDelivery = Delivery.builder()
                .id(1L)
                .deliveryCode("DEL-001")
                .price(150.0)
                .build();

        testPayment = Payment.builder()
                .id(1L)
                .user(testUser)
                .delivery(testDelivery)
                .amount(BigDecimal.valueOf(150.00))
                .currency("ETB")
                .provider(PaymentProvider.TELEBIRR)
                .status(PaymentStatus.PENDING)
                .transactionRef("TEL_123456789")
                .description("Test payment")
                .active(true)
                .build();

        testPaymentResponseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .userName("Test User")
                .deliveryId(1L)
                .deliveryCode("DEL-001")
                .amount(BigDecimal.valueOf(150.00))
                .currency("ETB")
                .provider(PaymentProvider.TELEBIRR)
                .status(PaymentStatus.PENDING)
                .transactionRef("TEL_123456789")
                .description("Test payment")
                .build();
    }

    @Test
    void initiatePayment_Success() {
        // Arrange
        Long userId = 1L;
        Long deliveryId = 1L;
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String phoneNumber = "+251912345678";
        String description = "Test payment";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(testDelivery));
        when(paymentRepository.findByDeliveryIdAndStatusAndActiveTrue(deliveryId, PaymentStatus.COMPLETED))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(paymentProviderFactory.getProvider(provider)).thenReturn(paymentProviderService);

        PaymentResponse providerResponse = PaymentResponse.builder()
                .success(true)
                .transactionRef("TEL_123456789")
                .status(PaymentStatus.PROCESSING)
                .message("Payment initiated successfully")
                .build();

        when(paymentProviderService.initiatePayment(any(PaymentRequest.class))).thenReturn(providerResponse);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.initiatePayment(userId, deliveryId, provider, amount, phoneNumber, description);

        // Assert
        assertNotNull(result);
        assertEquals(testPaymentResponseDTO.getId(), result.getId());
        assertEquals(testPaymentResponseDTO.getTransactionRef(), result.getTransactionRef());
        assertEquals(PaymentStatus.PENDING, result.getStatus());

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(paymentProviderService).initiatePayment(any(PaymentRequest.class));
    }

    @Test
    void initiatePayment_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long deliveryId = 1L;
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String phoneNumber = "+251912345678";
        String description = "Test payment";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.initiatePayment(userId, deliveryId, provider, amount, phoneNumber, description));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void initiatePayment_DeliveryNotFound() {
        // Arrange
        Long userId = 1L;
        Long deliveryId = 999L;
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String phoneNumber = "+251912345678";
        String description = "Test payment";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.initiatePayment(userId, deliveryId, provider, amount, phoneNumber, description));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void initiatePayment_PaymentAlreadyCompleted() {
        // Arrange
        Long userId = 1L;
        Long deliveryId = 1L;
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String phoneNumber = "+251912345678";
        String description = "Test payment";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(testDelivery));
        when(paymentRepository.findByDeliveryIdAndStatusAndActiveTrue(deliveryId, PaymentStatus.COMPLETED))
                .thenReturn(Optional.of(testPayment));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                paymentService.initiatePayment(userId, deliveryId, provider, amount, phoneNumber, description));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmPayment_Success() {
        // Arrange
        String transactionRef = "TEL_123456789";
        testPayment.setStatus(PaymentStatus.PROCESSING);

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.of(testPayment));
        when(paymentProviderFactory.getProvider(PaymentProvider.TELEBIRR)).thenReturn(paymentProviderService);

        PaymentResponse providerResponse = PaymentResponse.builder()
                .success(true)
                .transactionRef(transactionRef)
                .status(PaymentStatus.COMPLETED)
                .message("Payment confirmed successfully")
                .build();

        when(paymentProviderService.confirmPayment(transactionRef)).thenReturn(providerResponse);
        when(paymentRepository.save(testPayment)).thenReturn(testPayment);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.confirmPayment(transactionRef);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, testPayment.getStatus());
        assertEquals(transactionRef, testPayment.getTransactionRef());

        verify(paymentRepository).save(testPayment);
        verify(paymentProviderService).confirmPayment(transactionRef);
    }

    @Test
    void confirmPayment_PaymentNotFound() {
        // Arrange
        String transactionRef = "INVALID_REF";

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.confirmPayment(transactionRef));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmPayment_AlreadyCompleted() {
        // Arrange
        String transactionRef = "TEL_123456789";
        testPayment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.confirmPayment(transactionRef);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, testPayment.getStatus());

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentProviderService, never()).confirmPayment(anyString());
    }

    @Test
    void confirmPayment_ProviderFailure() {
        // Arrange
        String transactionRef = "TEL_123456789";
        testPayment.setStatus(PaymentStatus.PROCESSING);

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.of(testPayment));
        when(paymentProviderFactory.getProvider(PaymentProvider.TELEBIRR)).thenReturn(paymentProviderService);

        PaymentResponse providerResponse = PaymentResponse.builder()
                .success(false)
                .transactionRef(transactionRef)
                .status(PaymentStatus.FAILED)
                .message("Payment confirmation failed")
                .errorCode("TEL_002")
                .build();

        when(paymentProviderService.confirmPayment(transactionRef)).thenReturn(providerResponse);
        when(paymentRepository.save(testPayment)).thenReturn(testPayment);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.confirmPayment(transactionRef);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.FAILED, testPayment.getStatus());
        assertEquals("Payment confirmation failed", testPayment.getFailureReason());

        verify(paymentRepository).save(testPayment);
        verify(paymentProviderService).confirmPayment(transactionRef);
    }

    @Test
    void getPaymentById_Success() {
        // Arrange
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.getPaymentById(paymentId);

        // Assert
        assertNotNull(result);
        assertEquals(testPaymentResponseDTO.getId(), result.getId());
    }

    @Test
    void getPaymentById_NotFound() {
        // Arrange
        Long paymentId = 999L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.getPaymentById(paymentId));
    }

    @Test
    void getPaymentByTransactionRef_Success() {
        // Arrange
        String transactionRef = "TEL_123456789";

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        PaymentResponseDTO result = paymentService.getPaymentByTransactionRef(transactionRef);

        // Assert
        assertNotNull(result);
        assertEquals(testPaymentResponseDTO.getTransactionRef(), result.getTransactionRef());
    }

    @Test
    void getPaymentByTransactionRef_NotFound() {
        // Arrange
        String transactionRef = "INVALID_REF";

        when(paymentRepository.findByTransactionRefAndActiveTrue(transactionRef))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.getPaymentByTransactionRef(transactionRef));
    }

    @Test
    void getPaymentsByUser_Success() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> paymentPage = new PageImpl<>(List.of(testPayment));

        when(paymentRepository.findByUserIdAndActiveTrue(userId, pageable)).thenReturn(paymentPage);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        Page<PaymentResponseDTO> result = paymentService.getPaymentsByUser(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPaymentResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getPaymentsByStatus_Success() {
        // Arrange
        PaymentStatus status = PaymentStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> paymentPage = new PageImpl<>(List.of(testPayment));

        when(paymentRepository.findByStatusAndActiveTrue(status, pageable)).thenReturn(paymentPage);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        Page<PaymentResponseDTO> result = paymentService.getPaymentsByStatus(status, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPaymentResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getPaymentsByProvider_Success() {
        // Arrange
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> paymentPage = new PageImpl<>(List.of(testPayment));

        when(paymentRepository.findByProviderAndActiveTrue(provider, pageable)).thenReturn(paymentPage);
        when(paymentMapper.toResponseDTO(testPayment)).thenReturn(testPaymentResponseDTO);

        // Act
        Page<PaymentResponseDTO> result = paymentService.getPaymentsByProvider(provider, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPaymentResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getPaymentsByDelivery_Success() {
        // Arrange
        Long deliveryId = 1L;
        List<Payment> payments = List.of(testPayment);

        when(paymentRepository.findByDeliveryIdAndActiveTrue(deliveryId)).thenReturn(payments);
        when(paymentMapper.toResponseDTOList(payments)).thenReturn(List.of(testPaymentResponseDTO));

        // Act
        List<PaymentResponseDTO> result = paymentService.getPaymentsByDelivery(deliveryId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getPaymentsByDateRange_Success() {
        // Arrange
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<Payment> payments = List.of(testPayment);

        when(paymentRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(payments);
        when(paymentMapper.toResponseDTOList(payments)).thenReturn(List.of(testPaymentResponseDTO));

        // Act
        List<PaymentResponseDTO> result = paymentService.getPaymentsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTotalPaymentsByUser_Success() {
        // Arrange
        Long userId = 1L;
        BigDecimal expectedTotal = BigDecimal.valueOf(1500.00);

        when(paymentRepository.sumAmountByUserId(userId)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = paymentService.getTotalPaymentsByUser(userId);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalPaymentsByStatus_Success() {
        // Arrange
        PaymentStatus status = PaymentStatus.COMPLETED;
        BigDecimal expectedTotal = BigDecimal.valueOf(5000.00);

        when(paymentRepository.sumAmountByStatus(status)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = paymentService.getTotalPaymentsByStatus(status);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalPaymentsByProvider_Success() {
        // Arrange
        PaymentProvider provider = PaymentProvider.TELEBIRR;
        BigDecimal expectedTotal = BigDecimal.valueOf(3000.00);

        when(paymentRepository.sumAmountByProvider(provider)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = paymentService.getTotalPaymentsByProvider(provider);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getFailedPayments_Success() {
        // Arrange
        List<Payment> failedPayments = List.of(testPayment);

        when(paymentRepository.findFailedPayments()).thenReturn(failedPayments);
        when(paymentMapper.toResponseDTOList(failedPayments)).thenReturn(List.of(testPaymentResponseDTO));

        // Act
        List<PaymentResponseDTO> result = paymentService.getFailedPayments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getPendingPaymentsOlderThan_Success() {
        // Arrange
        Instant cutoffTime = Instant.now().minusSeconds(3600);
        List<Payment> pendingPayments = List.of(testPayment);

        when(paymentRepository.findPendingPaymentsOlderThan(cutoffTime)).thenReturn(pendingPayments);
        when(paymentMapper.toResponseDTOList(pendingPayments)).thenReturn(List.of(testPaymentResponseDTO));

        // Act
        List<PaymentResponseDTO> result = paymentService.getPendingPaymentsOlderThan(cutoffTime);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPaymentResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getSupportedProviders_Success() {
        // Arrange
        List<PaymentProvider> expectedProviders = List.of(
                PaymentProvider.TELEBIRR,
                PaymentProvider.CBE_BIRR,
                PaymentProvider.M_BIRR,
                PaymentProvider.HELLOCASH,
                PaymentProvider.AMOLE
        );

        when(paymentProviderFactory.getSupportedProviders()).thenReturn(expectedProviders);

        // Act
        List<PaymentProvider> result = paymentService.getSupportedProviders();

        // Assert
        assertEquals(expectedProviders, result);
    }
}
