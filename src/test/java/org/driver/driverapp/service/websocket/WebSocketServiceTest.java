package org.driver.driverapp.service.websocket;

import org.driver.driverapp.dto.websocket.DeliveryStatusDTO;
import org.driver.driverapp.dto.websocket.DriverLocationDTO;
import org.driver.driverapp.dto.websocket.PartnerUpdateDTO;
import org.driver.driverapp.dto.websocket.WebSocketMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketService webSocketService;

    private DriverLocationDTO testDriverLocation;
    private DeliveryStatusDTO testDeliveryStatus;
    private PartnerUpdateDTO testPartnerUpdate;

    @BeforeEach
    void setUp() {
        testDriverLocation = DriverLocationDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .speedKmh(25.0)
                .headingDegrees(45.0)
                .timestamp(Instant.now())
                .driverName("Driver 1")
                .vehicleInfo("Vehicle 1")
                .deliveryStatus("IN_PROGRESS")
                .build();

        testDeliveryStatus = DeliveryStatusDTO.builder()
                .deliveryId(1L)
                .status("IN_PROGRESS")
                .timestamp(Instant.now())
                .message("Driver is on the way")
                .driverId(1L)
                .driverName("Driver 1")
                .customerId(1L)
                .partnerId(1L)
                .estimatedArrival("15 minutes")
                .currentLocation("Near Bole")
                .build();

        testPartnerUpdate = PartnerUpdateDTO.builder()
                .partnerId(1L)
                .deliveryId(1L)
                .updateType("NEW_DELIVERY")
                .message("New delivery assigned")
                .timestamp(Instant.now())
                .deliveryStatus("ASSIGNED")
                .driverId(1L)
                .driverName("Driver 1")
                .customerName("Customer 1")
                .deliveryAddress("Bole, Addis Ababa")
                .estimatedArrival("30 minutes")
                .totalAmount(150.0)
                .build();
    }

    @Test
    void sendDriverLocationUpdate_Success() {
        // Act
        webSocketService.sendDriverLocationUpdate(testDriverLocation);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/driver/1/location"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendDeliveryStatusUpdate_Success() {
        // Act
        webSocketService.sendDeliveryStatusUpdate(testDeliveryStatus);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/delivery/1/status"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendPartnerUpdate_Success() {
        // Act
        webSocketService.sendPartnerUpdate(testPartnerUpdate);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/partner/1/updates"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendDriverLocationUpdateToUser_Success() {
        // Arrange
        String username = "driver1";
        Long driverId = 1L;

        // Act
        webSocketService.sendDriverLocationUpdateToUser(driverId, username, testDriverLocation);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(username),
                eq("/queue/driver/1/location"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendDeliveryStatusUpdateToUser_Success() {
        // Arrange
        String username = "customer1";
        Long deliveryId = 1L;

        // Act
        webSocketService.sendDeliveryStatusUpdateToUser(deliveryId, username, testDeliveryStatus);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(username),
                eq("/queue/delivery/1/status"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendPartnerUpdateToUser_Success() {
        // Arrange
        String username = "partner1";
        Long partnerId = 1L;

        // Act
        webSocketService.sendPartnerUpdateToUser(partnerId, username, testPartnerUpdate);

        // Assert
        verify(messagingTemplate).convertAndSendToUser(
                eq(username),
                eq("/queue/partner/1/updates"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendBroadcastMessage_Success() {
        // Arrange
        String message = "System maintenance scheduled";
        String type = "MAINTENANCE";

        // Act
        webSocketService.sendBroadcastMessage(message, type);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/broadcast"),
                any(WebSocketMessage.class)
        );
    }

    @Test
    void sendMultipleDriverLocationUpdates_Success() {
        // Arrange
        DriverLocationDTO location1 = DriverLocationDTO.builder()
                .driverId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .build();

        DriverLocationDTO location2 = DriverLocationDTO.builder()
                .driverId(2L)
                .lat(9.1500)
                .longitude(40.5000)
                .build();

        // Act
        webSocketService.sendDriverLocationUpdate(location1);
        webSocketService.sendDriverLocationUpdate(location2);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/driver/1/location"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/driver/2/location"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(WebSocketMessage.class));
    }

    @Test
    void sendMultipleDeliveryStatusUpdates_Success() {
        // Arrange
        DeliveryStatusDTO status1 = DeliveryStatusDTO.builder()
                .deliveryId(1L)
                .status("PICKED_UP")
                .build();

        DeliveryStatusDTO status2 = DeliveryStatusDTO.builder()
                .deliveryId(2L)
                .status("DELIVERED")
                .build();

        // Act
        webSocketService.sendDeliveryStatusUpdate(status1);
        webSocketService.sendDeliveryStatusUpdate(status2);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/delivery/1/status"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/delivery/2/status"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(WebSocketMessage.class));
    }

    @Test
    void sendMultiplePartnerUpdates_Success() {
        // Arrange
        PartnerUpdateDTO update1 = PartnerUpdateDTO.builder()
                .partnerId(1L)
                .updateType("NEW_DELIVERY")
                .build();

        PartnerUpdateDTO update2 = PartnerUpdateDTO.builder()
                .partnerId(2L)
                .updateType("STATUS_CHANGE")
                .build();

        // Act
        webSocketService.sendPartnerUpdate(update1);
        webSocketService.sendPartnerUpdate(update2);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/partner/1/updates"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate).convertAndSend(
                eq("/topic/partner/2/updates"),
                any(WebSocketMessage.class)
        );
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(WebSocketMessage.class));
    }
}
