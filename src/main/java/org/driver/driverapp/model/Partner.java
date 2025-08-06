package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;          // e.g., MedPlus Pharmacy
    private String contactPhone;

    @Embedded
    private Address address;
}
