package org.driver.driverapp.repository;

import jakarta.validation.ConstraintViolationException;
import org.driver.driverapp.enums.Role;
import org.driver.driverapp.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_crud_user() {
        User user = User.builder()
                .username("admin")
                .password("hashed-password")
                .role(Role.ADMIN)
                .email("admin@example.com")
                .build();

        user = userRepository.saveAndFlush(user);
        Assertions.assertNotNull(user.getId());

        User found = userRepository.findById(user.getId()).orElseThrow();
        Assertions.assertEquals("admin", found.getUsername());
    }

    @Test
    void should_validate_username_not_blank() {
        User user = User.builder()
                .username("")
                .password("hashed")
                .role(Role.ADMIN)
                .build();
        Assertions.assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }
}


