package org.driver.driverapp.init;

import org.driver.driverapp.model.Address;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final PartnerRepository partnerRepository;

    @Override
    public void run(String... args) {
        if (partnerRepository.count() == 0) {
            Address address = new Address();
            address.setCity("Bahir Dar");
            address.setLatitude(11.5936);      // Example GPS location
            address.setLongitude(37.3908);
            address.setLocationNote("Near the big blue gate, close to Bahir Dar Mall");

            Partner partner = new Partner();
            partner.setName("Test Pharmacy Bahir Dar");
            partner.setAddress(address);

            partnerRepository.save(partner);
            System.out.println("Default partner with GPS address saved.");
        }
    }
}
