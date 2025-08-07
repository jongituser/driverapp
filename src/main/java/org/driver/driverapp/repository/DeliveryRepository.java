package org.driver.driverapp.repository;

import org.driver.driverapp.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    //TODO You can add custom queries here later if needed
}
