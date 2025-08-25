package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.websocket.DeliveryStatusDTO;
import org.driver.driverapp.dto.websocket.DriverLocationDTO;
import org.driver.driverapp.dto.websocket.PartnerUpdateDTO;
import org.driver.driverapp.service.websocket.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketService webSocketService;

    @MessageMapping("/driver/location")
    @SendTo("/topic/driver/{driverId}/location")
    public DriverLocationDTO handleDriverLocationUpdate(DriverLocationDTO locationUpdate) {
        log.info("Received driver location update: {}", locationUpdate);
        
        // Update timestamp
        locationUpdate.setTimestamp(Instant.now());
        
        // Send to all subscribers
        webSocketService.sendDriverLocationUpdate(locationUpdate);
        
        return locationUpdate;
    }

    @MessageMapping("/delivery/status")
    @SendTo("/topic/delivery/{deliveryId}/status")
    public DeliveryStatusDTO handleDeliveryStatusUpdate(DeliveryStatusDTO statusUpdate) {
        log.info("Received delivery status update: {}", statusUpdate);
        
        // Update timestamp
        statusUpdate.setTimestamp(Instant.now());
        
        // Send to all subscribers
        webSocketService.sendDeliveryStatusUpdate(statusUpdate);
        
        return statusUpdate;
    }

    @MessageMapping("/partner/update")
    @SendTo("/topic/partner/{partnerId}/updates")
    public PartnerUpdateDTO handlePartnerUpdate(PartnerUpdateDTO partnerUpdate) {
        log.info("Received partner update: {}", partnerUpdate);
        
        // Update timestamp
        partnerUpdate.setTimestamp(Instant.now());
        
        // Send to all subscribers
        webSocketService.sendPartnerUpdate(partnerUpdate);
        
        return partnerUpdate;
    }

    @MessageMapping("/driver/location/private")
    @SendToUser("/queue/driver/location")
    public DriverLocationDTO handlePrivateDriverLocationUpdate(DriverLocationDTO locationUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Received private driver location update from user: {}", username);
        
        // Update timestamp
        locationUpdate.setTimestamp(Instant.now());
        
        // Send to specific user
        webSocketService.sendDriverLocationUpdateToUser(
                locationUpdate.getDriverId(), username, locationUpdate);
        
        return locationUpdate;
    }

    @MessageMapping("/delivery/status/private")
    @SendToUser("/queue/delivery/status")
    public DeliveryStatusDTO handlePrivateDeliveryStatusUpdate(DeliveryStatusDTO statusUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Received private delivery status update from user: {}", username);
        
        // Update timestamp
        statusUpdate.setTimestamp(Instant.now());
        
        // Send to specific user
        webSocketService.sendDeliveryStatusUpdateToUser(
                statusUpdate.getDeliveryId(), username, statusUpdate);
        
        return statusUpdate;
    }

    @MessageMapping("/partner/update/private")
    @SendToUser("/queue/partner/updates")
    public PartnerUpdateDTO handlePrivatePartnerUpdate(PartnerUpdateDTO partnerUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Received private partner update from user: {}", username);
        
        // Update timestamp
        partnerUpdate.setTimestamp(Instant.now());
        
        // Send to specific user
        webSocketService.sendPartnerUpdateToUser(
                partnerUpdate.getPartnerId(), username, partnerUpdate);
        
        return partnerUpdate;
    }

    @MessageMapping("/broadcast")
    @SendTo("/topic/broadcast")
    public String handleBroadcastMessage(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Received broadcast message from user: {}: {}", username, message);
        
        // Send broadcast message
        webSocketService.sendBroadcastMessage(message, "BROADCAST");
        
        return message;
    }
}
