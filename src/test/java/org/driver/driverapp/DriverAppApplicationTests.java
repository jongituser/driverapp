package org.driver.driverapp;

import org.driver.driverapp.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@Import(TestConfig.class)
class DriverAppApplicationTests {

    @Test
    void contextLoads() {
    }

}
