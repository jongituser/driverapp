package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.websocket.DeliveryStatusDTO;
import org.driver.driverapp.dto.websocket.DriverLocationDTO;
import org.driver.driverapp.dto.websocket.PartnerUpdateDTO;
import org.driver.driverapp.service.websocket.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/websocket")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final WebSocketService webSocketService;

    @PostMapping("/driver/{driverId}/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<String> simulateDriverLocationUpdate(
            @PathVariable Long driverId,
            @RequestBody DriverLocationDTO locationUpdate) {
        
        locationUpdate.setDriverId(driverId);
        locationUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendDriverLocationUpdate(locationUpdate);
        
        log.info("Simulated driver location update for driver: {}", driverId);
        return ResponseEntity.ok("Driver location update sent");
    }

    @PostMapping("/delivery/{deliveryId}/status")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<String> simulateDeliveryStatusUpdate(
            @PathVariable Long deliveryId,
            @RequestBody DeliveryStatusDTO statusUpdate) {
        
        statusUpdate.setDeliveryId(deliveryId);
        statusUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendDeliveryStatusUpdate(statusUpdate);
        
        log.info("Simulated delivery status update for delivery: {}", deliveryId);
        return ResponseEntity.ok("Delivery status update sent");
    }

    @PostMapping("/partner/{partnerId}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    public ResponseEntity<String> simulatePartnerUpdate(
            @PathVariable Long partnerId,
            @RequestBody PartnerUpdateDTO partnerUpdate) {
        
        partnerUpdate.setPartnerId(partnerId);
        partnerUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendPartnerUpdate(partnerUpdate);
        
        log.info("Simulated partner update for partner: {}", partnerId);
        return ResponseEntity.ok("Partner update sent");
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendBroadcastMessage(@RequestBody String message) {
        webSocketService.sendBroadcastMessage(message, "BROADCAST");
        
        log.info("Broadcast message sent: {}", message);
        return ResponseEntity.ok("Broadcast message sent");
    }

    @PostMapping("/driver/{driverId}/location/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendDriverLocationToUser(
            @PathVariable Long driverId,
            @PathVariable String username,
            @RequestBody DriverLocationDTO locationUpdate) {
        
        locationUpdate.setDriverId(driverId);
        locationUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendDriverLocationUpdateToUser(driverId, username, locationUpdate);
        
        log.info("Sent driver location update to user: {} for driver: {}", username, driverId);
        return ResponseEntity.ok("Driver location update sent to user");
    }

    @PostMapping("/delivery/{deliveryId}/status/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendDeliveryStatusToUser(
            @PathVariable Long deliveryId,
            @PathVariable String username,
            @RequestBody DeliveryStatusDTO statusUpdate) {
        
        statusUpdate.setDeliveryId(deliveryId);
        statusUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendDeliveryStatusUpdateToUser(deliveryId, username, statusUpdate);
        
        log.info("Sent delivery status update to user: {} for delivery: {}", username, deliveryId);
        return ResponseEntity.ok("Delivery status update sent to user");
    }

    @PostMapping("/partner/{partnerId}/update/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendPartnerUpdateToUser(
            @PathVariable Long partnerId,
            @PathVariable String username,
            @RequestBody PartnerUpdateDTO partnerUpdate) {
        
        partnerUpdate.setPartnerId(partnerId);
        partnerUpdate.setTimestamp(Instant.now());
        
        webSocketService.sendPartnerUpdateToUser(partnerId, username, partnerUpdate);
        
        log.info("Sent partner update to user: {} for partner: {}", username, partnerId);
        return ResponseEntity.ok("Partner update sent to user");
    }
}
