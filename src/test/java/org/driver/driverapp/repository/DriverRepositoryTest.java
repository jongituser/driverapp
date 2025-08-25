package org.driver.driverapp.repository;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_crud_driver_with_user_relationship() {
        User user = User.builder()
                .username("0700000000")
                .password("hashed-password")
                .role(org.driver.driverapp.enums.Role.DRIVER)
                .build();
        user = userRepository.save(user);

        Driver driver = Driver.builder()
                .name("John Doe")
                .phoneNumber("0700000000")
                .email("john@example.com")
                .status(DriverStatus.AVAILABLE)
                .user(user)
                .build();

        driver = driverRepository.saveAndFlush(driver);

        Optional<Driver> found = driverRepository.findById(driver.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("John Doe", found.get().getName());
    }

    @Test
    void should_enforce_name_not_blank() {
        Driver driver = Driver.builder()
                .name("")
                .phoneNumber("0700000001")
                .status(DriverStatus.AVAILABLE)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            driverRepository.saveAndFlush(driver);
        });
    }

    @Test
    void should_detect_optimistic_lock_conflict() {
        Driver driver = Driver.builder()
                .name("Jane Doe")
                .phoneNumber("0700000002")
                .status(DriverStatus.AVAILABLE)
                .build();
        driver = driverRepository.saveAndFlush(driver);

        Driver d1 = entityManager.find(Driver.class, driver.getId());
        Driver d2 = entityManager.find(Driver.class, driver.getId());

        d1.setName("Jane A");
        driverRepository.saveAndFlush(d1);

        d2.setName("Jane B");
        Long versionBefore = d1.getVersion();
        driverRepository.saveAndFlush(d1);
        Driver reloaded = driverRepository.findById(driver.getId()).orElseThrow();
        Assertions.assertTrue(reloaded.getVersion() > versionBefore);
    }
}


