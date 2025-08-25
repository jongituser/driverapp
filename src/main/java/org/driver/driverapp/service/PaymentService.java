package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentProviderFactory paymentProviderFactory;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponseDTO initiatePayment(Long userId, Long deliveryId, PaymentProvider provider, 
                                            BigDecimal amount, String phoneNumber, String description) {
        log.info("Initiating payment for user: {}, delivery: {}, provider: {}, amount: {}", 
                userId, deliveryId, provider, amount);

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate delivery
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        // Check if payment already exists for this delivery
        Optional<Payment> existingPayment = paymentRepository.findByDeliveryIdAndStatusAndActiveTrue(
                deliveryId, PaymentStatus.COMPLETED);
        if (existingPayment.isPresent()) {
            throw new IllegalStateException("Payment already completed for delivery: " + deliveryId);
        }

        // Create payment record
        Payment payment = Payment.builder()
                .user(user)
                .delivery(delivery)
                .amount(amount)
                .currency("ETB")
                .provider(provider)
                .status(PaymentStatus.PENDING)
                .description(description)
                .active(true)
                .build();

        payment = paymentRepository.save(payment);

        // Initiate payment with provider
        PaymentProviderService providerService = paymentProviderFactory.getProvider(provider);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .userId(userId.toString())
                .deliveryId(deliveryId.toString())
                .amount(amount)
                .currency("ETB")
                .provider(provider)
                .phoneNumber(phoneNumber)
                .description(description)
                .build();

        PaymentResponse providerResponse = providerService.initiatePayment(paymentRequest);

        if (providerResponse.isSuccess()) {
            payment.setTransactionRef(providerResponse.getTransactionRef());
            payment.setStatus(providerResponse.getStatus());
            payment = paymentRepository.save(payment);
            log.info("Payment initiated successfully: {}", payment.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(providerResponse.getMessage());
            payment = paymentRepository.save(payment);
            log.error("Payment initiation failed: {}", providerResponse.getMessage());
        }

        return paymentMapper.toResponseDTO(payment);
    }

    @Transactional
    public PaymentResponseDTO confirmPayment(String transactionRef) {
        log.info("Confirming payment with transaction ref: {}", transactionRef);

        Payment payment = paymentRepository.findByTransactionRefAndActiveTrue(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ref: " + transactionRef));

        if (payment.isCompleted()) {
            log.info("Payment already completed: {}", transactionRef);
            return paymentMapper.toResponseDTO(payment);
        }

        // Confirm payment with provider
        PaymentProviderService providerService = paymentProviderFactory.getProvider(payment.getProvider());
        PaymentResponse providerResponse = providerService.confirmPayment(transactionRef);

        if (providerResponse.isSuccess()) {
            payment.markAsCompleted(transactionRef);
            log.info("Payment confirmed successfully: {}", transactionRef);
        } else {
            payment.markAsFailed(providerResponse.getMessage());
            log.error("Payment confirmation failed: {}", providerResponse.getMessage());
        }

        payment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByTransactionRef(String transactionRef) {
        Payment payment = paymentRepository.findByTransactionRefAndActiveTrue(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ref: " + transactionRef));
        return paymentMapper.toResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByUser(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserIdAndActiveTrue(userId, pageable);
        return payments.map(paymentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByStatusAndActiveTrue(status, pageable);
        return payments.map(paymentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByProvider(PaymentProvider provider, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByProviderAndActiveTrue(provider, pageable);
        return payments.map(paymentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByDelivery(Long deliveryId) {
        List<Payment> payments = paymentRepository.findByDeliveryIdAndActiveTrue(deliveryId);
        return paymentMapper.toResponseDTOList(payments);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByDateRange(Instant startDate, Instant endDate) {
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDate, endDate);
        return paymentMapper.toResponseDTOList(payments);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsByUser(Long userId) {
        return paymentRepository.sumAmountByUserId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.sumAmountByStatus(status);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsByProvider(PaymentProvider provider) {
        return paymentRepository.sumAmountByProvider(provider);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getFailedPayments() {
        List<Payment> payments = paymentRepository.findFailedPayments();
        return paymentMapper.toResponseDTOList(payments);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPendingPaymentsOlderThan(Instant cutoffTime) {
        List<Payment> payments = paymentRepository.findPendingPaymentsOlderThan(cutoffTime);
        return paymentMapper.toResponseDTOList(payments);
    }

    @Transactional(readOnly = true)
    public List<PaymentProvider> getSupportedProviders() {
        return paymentProviderFactory.getSupportedProviders();
    }
}
