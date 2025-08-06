package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.enums.DriverStatus;
import org.driver.driverapp.model.*;
import org.driver.driverapp.repository.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.driver.driverapp.dto.DeliveryAnalyticsDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveries;
    private final DriverRepository drivers;
    private final PartnerRepository partners;

    // ---------------------------------------------
    // CREATE DELIVERY
    // ---------------------------------------------
    public Delivery create(DeliveryRequest req) {
        Partner partner = partners.findById(req.getPickupPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        OffsetDateTime now = OffsetDateTime.now();
        int eta = ThreadLocalRandom.current().nextInt(15, 46); // 15-45 min
        Delivery delivery = Delivery.builder()
                .pickupPartner(partner)
                .recipientName(req.getRecipientName())
                .recipientPhone(req.getRecipientPhone())
                .dropoffAddress(req.getDropoffAddress())
                .status(DeliveryStatus.PENDING)
                .createdAt(now)
                .etaMinutes(eta)
                .dueAt(now.plusMinutes(eta))
                .build();

        if (req.getDriverId() != null) {
            assignDriver(delivery, req.getDriverId(), now);
        } else {
            autoAssignToBestDriver(delivery, now);
        }

        return deliveries.save(delivery);
    }

    private void assignDriver(Delivery delivery, Long driverId, OffsetDateTime now) {
        Driver driver = drivers.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        if (driver.getStatus() != DriverStatus.AVAILABLE)
            throw new IllegalStateException("Driver not available");

        delivery.setAssignedDriver(driver);
        delivery.setAssignedAt(now);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickedUpAt(now);
        delivery.setStartedAt(now);

        driver.setStatus(DriverStatus.BUSY);
        driver.setActiveDeliveries(driver.getActiveDeliveries() + 1);
        drivers.save(driver);
    }

    private void autoAssignToBestDriver(Delivery delivery, OffsetDateTime now) {
        List<Driver> available = drivers.findAll().stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .sorted(Comparator.comparingInt(Driver::getActiveDeliveries))
                .toList();

        if (!available.isEmpty()) {
            Driver driver = available.get(0);
            assignDriver(delivery, driver.getId(), now);
        }
    }

    // ---------------------------------------------
    // CLAIM DELIVERY (driver self-assigns)
    // ---------------------------------------------
    public Delivery claim(Long deliveryId, Long driverId) {
        Delivery delivery = deliveries.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
        if (delivery.getAssignedDriver() != null)
            throw new IllegalStateException("Delivery already assigned");

        Driver driver = drivers.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        if (driver.getStatus() != DriverStatus.AVAILABLE)
            throw new IllegalStateException("Driver not available");

        OffsetDateTime now = OffsetDateTime.now();
        delivery.setAssignedDriver(driver);
        delivery.setAssignedAt(now);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickedUpAt(now);
        delivery.setStartedAt(now);

        driver.setStatus(DriverStatus.BUSY);
        driver.setActiveDeliveries(driver.getActiveDeliveries() + 1);
        drivers.save(driver);

        return deliveries.save(delivery);
    }

    // ---------------------------------------------
    // AUTO-ASSIGN EXISTING DELIVERY
    // ---------------------------------------------
    public Delivery autoAssign(Long deliveryId) {
        Delivery delivery = deliveries.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
        if (delivery.getAssignedDriver() != null) return delivery;

        List<Driver> available = drivers.findAll().stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .sorted(Comparator.comparingInt(Driver::getActiveDeliveries))
                .toList();

        return available.stream()
                .findFirst()
                .map(driver -> claim(deliveryId, driver.getId()))
                .orElseThrow(() -> new IllegalStateException("No available drivers"));
    }

    // ---------------------------------------------
    // UPDATE STATUS
    // ---------------------------------------------
    public Delivery updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveries.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        OffsetDateTime now = OffsetDateTime.now();
        delivery.setStatus(status);

        switch (status) {
            case PICKED_UP -> {
                if (delivery.getPickedUpAt() == null) delivery.setPickedUpAt(now);
                if (delivery.getStartedAt() == null) delivery.setStartedAt(now);
            }
            case IN_TRANSIT -> {
                if (delivery.getStartedAt() == null) delivery.setStartedAt(now);
            }
            case DELIVERED -> {
                if (delivery.getPickedUpAt() == null) delivery.setPickedUpAt(now);
                delivery.setDeliveredAt(now);
                releaseDriver(delivery.getAssignedDriver());
            }
            default -> {}
        }

        return deliveries.save(delivery);
    }

    private void releaseDriver(Driver driver) {
        if (driver == null) return;

        driver.setActiveDeliveries(Math.max(0, driver.getActiveDeliveries() - 1));
        if (driver.getActiveDeliveries() == 0)
            driver.setStatus(DriverStatus.AVAILABLE);
        drivers.save(driver);
    }

    // ---------------------------------------------
    // OVERDUE CHECKER (Scheduled)
    // ---------------------------------------------
    @Scheduled(fixedRate = 60000)
    public void markOverdueDeliveries() {
        OffsetDateTime now = OffsetDateTime.now();

        List<Delivery> overdue = deliveries.findAll().stream()
                .filter(d -> d.getStatus() != DeliveryStatus.DELIVERED)
                .filter(d -> d.getStatus() != DeliveryStatus.OVERDUE)
                .filter(d -> d.getDueAt() != null && d.getDueAt().isBefore(now))
                .toList();

        for (Delivery d : overdue) {
            d.setStatus(DeliveryStatus.OVERDUE);
            deliveries.save(d);
            log.warn("Delivery ID {} is OVERDUE", d.getId());

            // TODO: Trigger alerts (email, webhook, SMS) when deliveries go overdue
            // Example ideas:
            // - emailService.sendOverdueNotification(d);
            // - webhookClient.postOverdueDelivery(d);
           // - smsService.sendAlert(d.getRecipientPhone(), "Your delivery is delayed");


            releaseDriver(d.getAssignedDriver());
        }

    }

    // ---------------------------------------------
    // OVERDUE FILTERING
    // ---------------------------------------------
    public List<Delivery> getOverdueDeliveriesFiltered(Long driverId, String city, String region, OffsetDateTime from, OffsetDateTime to) {
        return deliveries.findAll().stream()
                .filter(d -> d.getStatus() == DeliveryStatus.OVERDUE)
                .filter(d -> driverId == null || (d.getAssignedDriver() != null && d.getAssignedDriver().getId().equals(driverId)))
                .filter(d -> city == null || (d.getDropoffAddress() != null && city.equalsIgnoreCase(d.getDropoffAddress().getCity())))
                .filter(d -> from == null || (d.getDueAt() != null && !d.getDueAt().isBefore(from)))
                .filter(d -> to == null || (d.getDueAt() != null && !d.getDueAt().isAfter(to)))
                .toList();
    }

    public Page<Delivery> getOverdueDeliveriesFiltered(
            Long driverId, String city, String region, OffsetDateTime from, OffsetDateTime to, Pageable pageable) {

        List<Delivery> filtered = deliveries.findAll(pageable).stream()
                .filter(d -> d.getStatus() == DeliveryStatus.OVERDUE)
                .filter(d -> driverId == null || (d.getAssignedDriver() != null && d.getAssignedDriver().getId().equals(driverId)))
                .filter(d -> city == null || (d.getDropoffAddress() != null && city.equalsIgnoreCase(d.getDropoffAddress().getCity())))
                .filter(d -> from == null || (d.getDueAt() != null && !d.getDueAt().isBefore(from)))
                .filter(d -> to == null || (d.getDueAt() != null && !d.getDueAt().isAfter(to)))
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }



    public DeliveryAnalyticsDTO getAnalytics() {
        List<Delivery> all = deliveries.findAll();

        long total = all.size();
        long overdue = all.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.OVERDUE)
                .count();

        double avgEta = all.stream()
                .mapToInt(Delivery::getEtaMinutes)
                .average()
                .orElse(0);

        double avgDuration = all.stream()
                .filter(d -> d.getPickedUpAt() != null && d.getDeliveredAt() != null)
                .mapToDouble(d -> java.time.Duration.between(d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                .average()
                .orElse(0);

        return new DeliveryAnalyticsDTO(
                total,
                overdue,
                avgEta,
                avgDuration,
                Collections.emptyMap(),  // driverStats
                Collections.emptyMap()   // partnerStats
        );
    }


}

