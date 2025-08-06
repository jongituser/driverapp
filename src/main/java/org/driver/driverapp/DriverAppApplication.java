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
}