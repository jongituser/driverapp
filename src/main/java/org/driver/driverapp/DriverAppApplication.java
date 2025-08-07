package org.driver.driverapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DriverAppApplication {
    public static void main(String[] args)
    {SpringApplication.run(DriverAppApplication.class, args);}

}