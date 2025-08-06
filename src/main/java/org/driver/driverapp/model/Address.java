package org.driver.driverapp.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Address {
    private String line1;
    private String line2;
    private String city;
    private String region;   // e.g., Amhara
    private String postalCode;
    private String country;  // e.g., ET
}
