package org.driver.driverapp.repository;


import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Test
    void should_crud_delivery() {
        Partner partner = partnerRepository.save(Partner.builder().name("P1").phone("0700").build());
        Driver driver = driverRepository.save(Driver.builder().name("D1").phoneNumber("0701").status(org.driver.driverapp.enums.DriverStatus.AVAILABLE).build());

        Delivery delivery = Delivery.builder()
                .deliveryCode("DEL-1")
                .pickupPartner(partner)
                .dropoffAddress("Dest")
                .status("CREATED")
                .driver(driver)
                .build();

        delivery = deliveryRepository.saveAndFlush(delivery);
        Assertions.assertNotNull(delivery.getId());
    }
}


