package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.*;
import org.driver.driverapp.model.*;
import org.driver.driverapp.repository.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeliveryRepository deliveries;
    private final DriverRepository drivers;
    private final InventoryItemRepository inventory;
    private final PartnerRepository partners;

        public DashboardDTO getDashboard() {
            List<DriverSummaryDTO> topDrivers = List.of(
                    DriverSummaryDTO.builder()
                            .name("Amanuel Berhane")
                            .totalDeliveries(42)
                            .onTimePercentage(90.5)
                            .averageDeliveryTime(28.3)
                            .build(),
                    DriverSummaryDTO.builder()
                            .name("Sofia Tesfaye")
                            .totalDeliveries(35)
                            .onTimePercentage(94.2)
                            .averageDeliveryTime(26.1)
                            .build(),
                    DriverSummaryDTO.builder()
                            .name("Henok Dawit")
                            .totalDeliveries(28)
                            .onTimePercentage(89.7)
                            .averageDeliveryTime(31.6)
                            .build()
            );

            List<LowInventoryAlertDTO> lowInventory = List.of(
                    LowInventoryAlertDTO.builder()
                            .partnerName("St. Paul Clinic")
                            .productName("Paracetamol 500mg")
                            .quantity(6)
                            .build(),
                    LowInventoryAlertDTO.builder()
                            .partnerName("Hallelujah Pharmacy")
                            .productName("Vitamin C")
                            .quantity(4)
                            .build()
            );

            return DashboardDTO.builder()
                    .totalDeliveries(122)
                    .inProgressDeliveries(14)
                    .deliveredToday(27)
                    .overdueDeliveries(5)
                    .averageEtaMinutes(35.7)
                    .averageDeliveryDurationMinutes(32.4)
                    .topDrivers(topDrivers)
                    .lowInventoryAlerts(lowInventory)
                    .build();
        }



    /* public DashboardDTO getDashboard() {
        List<Delivery> all = deliveries.findAll();
        OffsetDateTime today = OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(OffsetDateTime.now().getOffset());

        long total = all.size();
        long inProgress = all.stream().filter(d -> d.getStatus() == DeliveryStatus.IN_PROGRESS).count();
        long deliveredToday = all.stream()
                .filter(d -> d.getDeliveredAt() != null && d.getDeliveredAt().isAfter(today))
                .count();
        long overdue = all.stream().filter(d -> d.getStatus() == DeliveryStatus.OVERDUE).count();

        double avgEta = all.stream()
                .mapToInt(Delivery::getEtaMinutes)
                .average().orElse(0);

        double avgDuration = all.stream()
                .filter(d -> d.getPickedUpAt() != null && d.getDeliveredAt() != null)
                .mapToDouble(d -> java.time.Duration.between(d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                .average().orElse(0);

        List<DriverSummaryDTO> topDrivers = drivers.findAll().stream()
                .map(driver -> {
                    List<Delivery> driverDeliveries = all.stream()
                            .filter(d -> d.getDriver() != null && d.getDriver().getId().equals(driver.getId()))
                            .collect(Collectors.toList());

                    long totalD = driverDeliveries.size();
                    long onTime = driverDeliveries.stream()
                            .filter(d -> d.getDeliveredAt() != null &&
                                    d.getEtaMinutes() >= java.time.Duration.between(
                                            d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                            .count();
                    double avg = driverDeliveries.stream()
                            .filter(d -> d.getPickedUpAt() != null && d.getDeliveredAt() != null)
                            .mapToDouble(d -> java.time.Duration.between(d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                            .average().orElse(0);

                    return DriverSummaryDTO.builder()
                            .name(driver.getName())
                            .totalDeliveries(totalD)
                            .onTimePercentage(totalD == 0 ? 0 : (onTime * 100.0 / totalD))
                            .averageDeliveryTime(avg)
                            .build();
                })
                .sorted(Comparator.comparingLong(DriverSummaryDTO::getTotalDeliveries).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<LowInventoryAlertDTO> lowInventory = inventory.findAll().stream()
                .filter(i -> i.getQuantity() < 10)
                .map(i -> LowInventoryAlertDTO.builder()
                        .partnerName(i.getPartner().getName())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalDeliveries(total)
                .inProgressDeliveries(inProgress)
                .deliveredToday(deliveredToday)
                .overdueDeliveries(overdue)
                .averageEtaMinutes(avgEta)
                .averageDeliveryDurationMinutes(avgDuration)
                .topDrivers(topDrivers)
                .lowInventoryAlerts(lowInventory)
                .build();

    }
     */
}
