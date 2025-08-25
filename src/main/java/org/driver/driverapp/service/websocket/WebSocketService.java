package org.driver.driverapp.service.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.websocket.DeliveryStatusDTO;
import org.driver.driverapp.dto.websocket.DriverLocationDTO;
import org.driver.driverapp.dto.websocket.PartnerUpdateDTO;
import org.driver.driverapp.dto.websocket.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendDriverLocationUpdate(DriverLocationDTO locationUpdate) {
        String destination = "/topic/driver/" + locationUpdate.getDriverId() + "/location";
        WebSocketMessage<DriverLocationDTO> message = WebSocketMessage.<DriverLocationDTO>builder()
                .type("DRIVER_LOCATION_UPDATE")
                .payload(locationUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSend(destination, message);
        log.info("Sent driver location update to: {}", destination);
    }

    public void sendDeliveryStatusUpdate(DeliveryStatusDTO statusUpdate) {
        String destination = "/topic/delivery/" + statusUpdate.getDeliveryId() + "/status";
        WebSocketMessage<DeliveryStatusDTO> message = WebSocketMessage.<DeliveryStatusDTO>builder()
                .type("DELIVERY_STATUS_UPDATE")
                .payload(statusUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSend(destination, message);
        log.info("Sent delivery status update to: {}", destination);
    }

    public void sendPartnerUpdate(PartnerUpdateDTO partnerUpdate) {
        String destination = "/topic/partner/" + partnerUpdate.getPartnerId() + "/updates";
        WebSocketMessage<PartnerUpdateDTO> message = WebSocketMessage.<PartnerUpdateDTO>builder()
                .type("PARTNER_UPDATE")
                .payload(partnerUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSend(destination, message);
        log.info("Sent partner update to: {}", destination);
    }

    public void sendDriverLocationUpdateToUser(Long driverId, String username, DriverLocationDTO locationUpdate) {
        String destination = "/user/" + username + "/queue/driver/" + driverId + "/location";
        WebSocketMessage<DriverLocationDTO> message = WebSocketMessage.<DriverLocationDTO>builder()
                .type("DRIVER_LOCATION_UPDATE")
                .payload(locationUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSendToUser(username, "/queue/driver/" + driverId + "/location", message);
        log.info("Sent driver location update to user: {} at destination: {}", username, destination);
    }

    public void sendDeliveryStatusUpdateToUser(Long deliveryId, String username, DeliveryStatusDTO statusUpdate) {
        String destination = "/user/" + username + "/queue/delivery/" + deliveryId + "/status";
        WebSocketMessage<DeliveryStatusDTO> message = WebSocketMessage.<DeliveryStatusDTO>builder()
                .type("DELIVERY_STATUS_UPDATE")
                .payload(statusUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSendToUser(username, "/queue/delivery/" + deliveryId + "/status", message);
        log.info("Sent delivery status update to user: {} at destination: {}", username, destination);
    }

    public void sendPartnerUpdateToUser(Long partnerId, String username, PartnerUpdateDTO partnerUpdate) {
        String destination = "/user/" + username + "/queue/partner/" + partnerId + "/updates";
        WebSocketMessage<PartnerUpdateDTO> message = WebSocketMessage.<PartnerUpdateDTO>builder()
                .type("PARTNER_UPDATE")
                .payload(partnerUpdate)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSendToUser(username, "/queue/partner/" + partnerId + "/updates", message);
        log.info("Sent partner update to user: {} at destination: {}", username, destination);
    }

    public void sendBroadcastMessage(String message, String type) {
        WebSocketMessage<String> wsMessage = WebSocketMessage.<String>builder()
                .type(type)
                .payload(message)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSend("/topic/broadcast", wsMessage);
        log.info("Sent broadcast message: {}", message);
    }
}
