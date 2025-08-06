package org.driver.driverapp.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Address {
    private String streetName;        // Optional
    private String houseNumber;       // Optional
    private String city;
    private String postalCode;        // Optional
    private double latitude;          // Required
    private double longitude;         // Required
    private String locationNote;      // Optional notes like "next to the blue church"
}
