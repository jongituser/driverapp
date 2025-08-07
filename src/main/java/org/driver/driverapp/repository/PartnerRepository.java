package org.driver.driverapp.repository;

import org.driver.driverapp.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByName(String name);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
