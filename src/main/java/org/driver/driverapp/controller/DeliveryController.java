package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.DeliveryAnalyticsDTO;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.model.*;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.driver.driverapp.service.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final PartnerRepository partnerRepository;
    private final DeliveryService deliveryService;

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDelivery(@PathVariable Long id) {
        return deliveryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@RequestBody DeliveryRequest request) {
        return ResponseEntity.ok(deliveryService.create(request));
    }

    @PostMapping("/{id}/assign/{driverId}")
    public ResponseEntity<Delivery> assignDelivery(@PathVariable Long id, @PathVariable Long driverId) {
        return ResponseEntity.ok(deliveryService.claim(id, driverId));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Delivery> updateStatus(@PathVariable Long id, @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }

    @PostMapping("/{id}/claim/{driverId}")
    public ResponseEntity<Delivery> claim(@PathVariable Long id, @PathVariable Long driverId) {
        return ResponseEntity.ok(deliveryService.claim(id, driverId));
    }

    @PostMapping("/{id}/auto-assign")
    public ResponseEntity<Delivery> autoAssign(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.autoAssign(id));
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueDeliveries(
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "false") boolean paged,
            @PageableDefault(size = 10, sort = "dueAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        OffsetDateTime fromTime = (from != null) ? OffsetDateTime.parse(from) : null;
        OffsetDateTime toTime = (to != null) ? OffsetDateTime.parse(to) : null;

        if (paged) {
            Page<Delivery> results = deliveryService.getOverdueDeliveriesFiltered(driverId, city, region, fromTime, toTime, pageable);
            return ResponseEntity.ok(results);
        } else {
            List<Delivery> results = deliveryService.getOverdueDeliveriesFiltered(driverId, city, region, fromTime, toTime);
            return ResponseEntity.ok(results);
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<DeliveryAnalyticsDTO> getAnalytics() {
        return ResponseEntity.ok(deliveryService.getAnalytics());
    }
}
