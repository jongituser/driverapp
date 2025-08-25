package org.driver.driverapp.service.payment;

import org.driver.driverapp.enums.PaymentProvider;

import java.math.BigDecimal;

public interface PaymentProviderService {
    
    PaymentProvider getProvider();
    
    PaymentResponse initiatePayment(PaymentRequest request);
    
    PaymentResponse confirmPayment(String transactionRef);
    
    PaymentResponse refundPayment(String transactionRef, BigDecimal amount);
    
    boolean isSupported(PaymentProvider provider);
}
