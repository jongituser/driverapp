package org.driver.driverapp.repository;

import org.driver.driverapp.model.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PartnerRepositoryTest {

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    void should_crud_partner() {
        Partner partner = Partner.builder()
                .name("Shop A")
                .phone("0700000003")
                .email("shop@example.com")
                .address("Main St")
                .city("City")
                .build();

        partner = partnerRepository.saveAndFlush(partner);
        Assertions.assertNotNull(partner.getId());
    }
}



