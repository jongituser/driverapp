package org.driver.driverapp.service.websocket;

import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketSecurityServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private WebSocketSecurityService webSocketSecurityService;

    private Authentication adminAuth;
    private Authentication driverAuth;
    private Authentication customerAuth;
    private Authentication partnerAuth;

    @BeforeEach
    void setUp() {
        adminAuth = new TestingAuthenticationToken(
                "admin",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        driverAuth = new TestingAuthenticationToken(
                "driver1",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );

        customerAuth = new TestingAuthenticationToken(
                "customer1",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        partnerAuth = new TestingAuthenticationToken(
                "partner1",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTNER"))
        );
    }

    @Test
    void validateSubscription_AdminCanSubscribeToAllTopics() {
        // Arrange
        String driverDestination = "/topic/driver/1/location";
        String deliveryDestination = "/topic/delivery/1/status";
        String partnerDestination = "/topic/partner/1/updates";

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(adminAuth, driverDestination));
        assertTrue(webSocketSecurityService.validateSubscription(adminAuth, deliveryDestination));
        assertTrue(webSocketSecurityService.validateSubscription(adminAuth, partnerDestination));
    }

    @Test
    void validateSubscription_DriverCanSubscribeToOwnLocation() {
        // Arrange
        String ownLocationDestination = "/topic/driver/1/location";
        String otherDriverLocationDestination = "/topic/driver/2/location";

        Driver driver = Driver.builder()
                .id(1L)
                .phoneNumber("driver1")
                .build();

        when(driverRepository.findByPhoneNumber("driver1")).thenReturn(Optional.of(driver));

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(driverAuth, ownLocationDestination));
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, otherDriverLocationDestination));
    }

    @Test
    void validateSubscription_CustomerCanSubscribeToOwnDelivery() {
        // Arrange
        String ownDeliveryDestination = "/topic/delivery/1/status";
        String otherDeliveryDestination = "/topic/delivery/2/status";

        when(deliveryRepository.findDeliveryIdByCustomerPhone("customer1"))
                .thenReturn(Optional.of(1L));

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(customerAuth, ownDeliveryDestination));
        assertFalse(webSocketSecurityService.validateSubscription(customerAuth, otherDeliveryDestination));
    }

    @Test
    void validateSubscription_PartnerCanSubscribeToOwnUpdates() {
        // Arrange
        String ownPartnerDestination = "/topic/partner/1/updates";
        String otherPartnerDestination = "/topic/partner/2/updates";

        Partner partner = Partner.builder()
                .id(1L)
                .phone("partner1")
                .build();

        when(partnerRepository.findByPhone("partner1")).thenReturn(Optional.of(partner));

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(partnerAuth, ownPartnerDestination));
        assertFalse(webSocketSecurityService.validateSubscription(partnerAuth, otherPartnerDestination));
    }

    @Test
    void validateSubscription_InvalidDestination_ReturnsFalse() {
        // Arrange
        String invalidDestination = "/topic/invalid/1/status";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, invalidDestination));
    }

    @Test
    void validateSubscription_InvalidEntityId_ReturnsFalse() {
        // Arrange
        String invalidIdDestination = "/topic/driver/invalid/location";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, invalidIdDestination));
    }

    @Test
    void validateSubscription_UnauthenticatedUser_ReturnsFalse() {
        // Arrange
        Authentication unauthenticatedAuth = new TestingAuthenticationToken(
                "user",
                "password",
                Collections.emptyList()
        );
        String destination = "/topic/driver/1/location";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(unauthenticatedAuth, destination));
    }

    @Test
    void validateSubscription_NullAuthentication_ReturnsFalse() {
        // Arrange
        String destination = "/topic/driver/1/location";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(null, destination));
    }

    @Test
    void validateSubscription_DriverNotFound_ReturnsFalse() {
        // Arrange
        String destination = "/topic/driver/1/location";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, destination));
    }

    @Test
    void validateSubscription_PartnerNotFound_ReturnsFalse() {
        // Arrange
        String destination = "/topic/partner/1/updates";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(partnerAuth, destination));
    }

    @Test
    void validateSubscription_DeliveryNotFound_ReturnsFalse() {
        // Arrange
        String destination = "/topic/delivery/1/status";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(customerAuth, destination));
    }

    @Test
    void validateSubscription_DriverCanSubscribeToOwnDelivery() {
        // Arrange
        String deliveryDestination = "/topic/delivery/1/status";

        when(deliveryRepository.findDeliveryIdByCustomerPhone("driver1")).thenReturn(Optional.empty());
        when(deliveryRepository.findDeliveryIdByDriverPhone("driver1")).thenReturn(Optional.of(1L));

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(driverAuth, deliveryDestination));
    }

    @Test
    void validateSubscription_PartnerCanSubscribeToOwnDelivery() {
        // Arrange
        String deliveryDestination = "/topic/delivery/1/status";

        when(deliveryRepository.findDeliveryIdByCustomerPhone("partner1")).thenReturn(Optional.empty());
        when(deliveryRepository.findDeliveryIdByDriverPhone("partner1")).thenReturn(Optional.empty());
        when(deliveryRepository.findDeliveryIdByPartnerPhone("partner1")).thenReturn(Optional.of(1L));

        // Act & Assert
        assertTrue(webSocketSecurityService.validateSubscription(partnerAuth, deliveryDestination));
    }

    @Test
    void validateSubscription_MalformedDestination_ReturnsFalse() {
        // Arrange
        String malformedDestination = "/topic/driver"; // Missing ID

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, malformedDestination));
    }

    @Test
    void validateSubscription_EmptyDestination_ReturnsFalse() {
        // Arrange
        String emptyDestination = "";

        // Act & Assert
        assertFalse(webSocketSecurityService.validateSubscription(driverAuth, emptyDestination));
    }
}
