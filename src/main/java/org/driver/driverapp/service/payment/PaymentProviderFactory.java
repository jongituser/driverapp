package org.driver.driverapp.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.enums.PaymentProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentProviderFactory {

    private final Map<PaymentProvider, PaymentProviderService> providerMap;

    public PaymentProviderFactory(List<PaymentProviderService> paymentProviders) {
        this.providerMap = paymentProviders.stream()
                .collect(Collectors.toMap(
                        PaymentProviderService::getProvider,
                        Function.identity()
                ));
    }

    public PaymentProviderService getProvider(PaymentProvider provider) {
        PaymentProviderService service = providerMap.get(provider);
        if (service == null) {
            throw new IllegalArgumentException("Payment provider not supported: " + provider);
        }
        return service;
    }

    public List<PaymentProvider> getSupportedProviders() {
        return providerMap.keySet().stream().toList();
    }

    public boolean isProviderSupported(PaymentProvider provider) {
        return providerMap.containsKey(provider);
    }
}
