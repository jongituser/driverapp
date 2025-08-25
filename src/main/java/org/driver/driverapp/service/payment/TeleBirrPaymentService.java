package org.driver.driverapp.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class TeleBirrPaymentService implements PaymentProviderService {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.TELEBIRR;
    }

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating TeleBirr payment for amount: {} ETB", request.getAmount());
        
        // Simulate API call to TeleBirr
        try {
            // Simulate processing time
            Thread.sleep(100);
            
            String transactionRef = "TEL_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            
            return PaymentResponse.builder()
                    .success(true)
                    .transactionRef(transactionRef)
                    .status(PaymentStatus.PROCESSING)
                    .message("Payment initiated successfully")
                    .build();
        } catch (Exception e) {
            log.error("TeleBirr payment initiation failed", e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment initiation failed")
                    .errorCode("TEL_001")
                    .build();
        }
    }

    @Override
    public PaymentResponse confirmPayment(String transactionRef) {
        log.info("Confirming TeleBirr payment: {}", transactionRef);
        
        // Simulate payment confirmation
        try {
            Thread.sleep(50);
            
            // Simulate 95% success rate
            boolean success = Math.random() > 0.05;
            
            if (success) {
                return PaymentResponse.builder()
                        .success(true)
                        .transactionRef(transactionRef)
                        .status(PaymentStatus.COMPLETED)
                        .message("Payment confirmed successfully")
                        .build();
            } else {
                return PaymentResponse.builder()
                        .success(false)
                        .transactionRef(transactionRef)
                        .status(PaymentStatus.FAILED)
                        .message("Payment confirmation failed")
                        .errorCode("TEL_002")
                        .build();
            }
        } catch (Exception e) {
            log.error("TeleBirr payment confirmation failed", e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment confirmation failed")
                    .errorCode("TEL_003")
                    .build();
        }
    }

    @Override
    public PaymentResponse refundPayment(String transactionRef, BigDecimal amount) {
        log.info("Processing TeleBirr refund: {} for amount: {}", transactionRef, amount);
        
        try {
            Thread.sleep(100);
            
            return PaymentResponse.builder()
                    .success(true)
                    .transactionRef("REF_" + transactionRef)
                    .status(PaymentStatus.COMPLETED)
                    .message("Refund processed successfully")
                    .build();
        } catch (Exception e) {
            log.error("TeleBirr refund failed", e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Refund failed")
                    .errorCode("TEL_004")
                    .build();
        }
    }

    @Override
    public boolean isSupported(PaymentProvider provider) {
        return PaymentProvider.TELEBIRR.equals(provider);
    }
}
