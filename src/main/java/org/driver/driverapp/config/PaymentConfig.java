package org.driver.driverapp.config;

import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.service.payment.GenericPaymentService;
import org.driver.driverapp.service.payment.PaymentProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean("telebirrPaymentService")
    public PaymentProviderService telebirrPaymentService() {
        return new GenericPaymentService(PaymentProvider.TELEBIRR);
    }

    @Bean("cbeBirrPaymentService")
    public PaymentProviderService cbeBirrPaymentService() {
        return new GenericPaymentService(PaymentProvider.CBE_BIRR);
    }

    @Bean("amolePaymentService")
    public PaymentProviderService amolePaymentService() {
        return new GenericPaymentService(PaymentProvider.AMOLE);
    }

    @Bean("hellocashPaymentService")
    public PaymentProviderService hellocashPaymentService() {
        return new GenericPaymentService(PaymentProvider.HELLOCASH);
    }

    @Bean("mBirrPaymentService")
    public PaymentProviderService mBirrPaymentService() {
        return new GenericPaymentService(PaymentProvider.M_BIRR);
    }

    @Bean("cashPaymentService")
    public PaymentProviderService cashPaymentService() {
        return new GenericPaymentService(PaymentProvider.CASH);
    }
}
