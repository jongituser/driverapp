package org.driver.driverapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "partners", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
})
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Partner name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phone;

    @Email(message = "Email should be valid")
    @Size(max = 100)
    private String email;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String city;

    @Size(max = 255)
    private String logoUrl;

    private boolean active = true;
}
