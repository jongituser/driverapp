package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.driver.driverapp.enums.Role;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Used for login (not email)

    @Column(nullable = false)
    private String password; // Hashed using BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(unique = true)
    private String email; // Used for contact / password recovery

    private boolean enabled = true;

    @OneToOne(mappedBy = "user")
    private Driver driver;
}
