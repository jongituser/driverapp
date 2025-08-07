package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Instant createdAt;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

}
