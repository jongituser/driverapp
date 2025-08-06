package org.driver.driverapp.repository;

import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByAssignedDriver(Driver driver);
}
