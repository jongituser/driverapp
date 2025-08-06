package org.driver.driverapp.scheduler;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.repository.DeliveryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

    @Component
    @RequiredArgsConstructor
    public class DeliverySlaWatcher {

        private final DeliveryRepository deliveryRepository;

        // Runs every minute
        @Scheduled(fixedRate = 60000)
        public void checkOverdueDeliveries() {
            OffsetDateTime now = OffsetDateTime.now();
            List<Delivery> all = deliveryRepository.findAll();

            for (Delivery d : all) {
                if (d.getDueAt() != null
                        && d.getDeliveredAt() == null
                        && now.isAfter(d.getDueAt())
                        && d.getStatus() != DeliveryStatus.OVERDUE) {

                    d.setStatus(DeliveryStatus.OVERDUE);
                    deliveryRepository.save(d);
                    System.out.println("Marked delivery ID " + d.getId() + " as OVERDUE");
                }
            }
        }
    }

