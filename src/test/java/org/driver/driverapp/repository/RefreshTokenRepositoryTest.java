package org.driver.driverapp.repository;

import org.driver.driverapp.enums.Role;
import org.driver.driverapp.model.RefreshToken;
import org.driver.driverapp.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_crud_refresh_token() {
        User user = userRepository.save(User.builder()
                .username("u1")
                .password("hashed-password")
                .role(Role.DRIVER)
                .build());

        RefreshToken token = RefreshToken.builder()
                .token("t1")
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .user(user)
                .build();

        token = refreshTokenRepository.saveAndFlush(token);
        Assertions.assertNotNull(token.getId());
    }
}


