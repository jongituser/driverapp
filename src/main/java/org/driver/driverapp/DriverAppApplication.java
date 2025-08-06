package org.driver.driverapp;

import org.driver.driverapp.model.Address;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DriverAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverAppApplication.class, args);
    }

    @Bean
    CommandLineRunner seedPartner(PartnerRepository partners) {
        return args -> {
            if (partners.count() == 0) {
                Partner clinic = Partner.builder()
                        .name("Test Clinic")
                        .contactPhone("+46 31 000 00 00")
                        .address(new Address(
                                "Storgatan 1",
                                null,
                                "Borås",
                                "Västra Götaland",
                                "503 31",
                                "SE"
                        ))
                        .build();
                partners.save(clinic);
            }
        };
    }
}