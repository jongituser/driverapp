package org.driver.driverapp.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class GenericPaymentService implements PaymentProviderService {

    private final PaymentProvider provider;
    private final String prefix;

    public GenericPaymentService(PaymentProvider provider) {
        this.provider = provider;
        this.prefix = getProviderPrefix(provider);
    }

    @Override
    public PaymentProvider getProvider() {
        return provider;
    }

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating {} payment for amount: {} ETB", provider, request.getAmount());
        
        try {
            Thread.sleep(80);
            
            String transactionRef = prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            
            return PaymentResponse.builder()
                    .success(true)
                    .transactionRef(transactionRef)
                    .status(PaymentStatus.PROCESSING)
                    .message("Payment initiated successfully")
                    .build();
        } catch (Exception e) {
            log.error("{} payment initiation failed", provider, e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment initiation failed")
                    .errorCode(prefix + "_001")
                    .build();
        }
    }

    @Override
    public PaymentResponse confirmPayment(String transactionRef) {
        log.info("Confirming {} payment: {}", provider, transactionRef);
        
        try {
            Thread.sleep(60);
            
            // Simulate 90% success rate for other providers
            boolean success = Math.random() > 0.10;
            
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
                        .errorCode(prefix + "_002")
                        .build();
            }
        } catch (Exception e) {
            log.error("{} payment confirmation failed", provider, e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment confirmation failed")
                    .errorCode(prefix + "_003")
                    .build();
        }
    }

    @Override
    public PaymentResponse refundPayment(String transactionRef, BigDecimal amount) {
        log.info("Processing {} refund: {} for amount: {}", provider, transactionRef, amount);
        
        try {
            Thread.sleep(90);
            
            return PaymentResponse.builder()
                    .success(true)
                    .transactionRef("REF_" + transactionRef)
                    .status(PaymentStatus.COMPLETED)
                    .message("Refund processed successfully")
                    .build();
        } catch (Exception e) {
            log.error("{} refund failed", provider, e);
            return PaymentResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Refund failed")
                    .errorCode(prefix + "_004")
                    .build();
        }
    }

    @Override
    public boolean isSupported(PaymentProvider provider) {
        return this.provider.equals(provider);
    }

    private String getProviderPrefix(PaymentProvider provider) {
        return switch (provider) {
            case CBE_BIRR -> "CBE";
            case M_BIRR -> "MBIR";
            case HELLOCASH -> "HCASH";
            case AMOLE -> "AMOLE";
            default -> "GEN";
        };
    }
}
