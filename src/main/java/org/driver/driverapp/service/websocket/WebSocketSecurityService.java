package org.driver.driverapp.service.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketSecurityService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final PartnerRepository partnerRepository;

    public boolean validateSubscription(Authentication authentication, String destination) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Admin can subscribe to all topics
        if (isAdmin) {
            return true;
        }

        // Parse destination to extract entity ID
        // Format: /topic/driver/1/location or /topic/delivery/1/status or /topic/partner/1/updates
        String[] parts = destination.split("/");
        if (parts.length < 4) {
            return false;
        }

        String topicType = parts[2]; // driver, delivery, partner (after /topic/)
        String entityId = parts[3]; // the ID

        try {
            Long id = Long.parseLong(entityId);
            
            switch (topicType) {
                case "driver":
                    return validateDriverSubscription(username, id);
                case "delivery":
                    return validateDeliverySubscription(username, id);
                case "partner":
                    return validatePartnerSubscription(username, id);
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid entity ID in WebSocket destination: {}", destination);
            return false;
        }
    }

    private boolean validateDriverSubscription(String username, Long driverId) {
        // Drivers can only subscribe to their own location updates
        return driverRepository.findByPhoneNumber(username)
                .map(driver -> driver.getId().equals(driverId))
                .orElse(false);
    }

    private boolean validateDeliverySubscription(String username, Long deliveryId) {
        // Check if user is associated with this delivery
        Optional<Long> userDeliveryId = deliveryRepository.findDeliveryIdByCustomerPhone(username)
                .or(() -> deliveryRepository.findDeliveryIdByDriverPhone(username))
                .or(() -> deliveryRepository.findDeliveryIdByPartnerPhone(username));

        return userDeliveryId.map(id -> id.equals(deliveryId)).orElse(false);
    }

    private boolean validatePartnerSubscription(String username, Long partnerId) {
        // Partners can only subscribe to their own updates
        return partnerRepository.findByPhone(username)
                .map(partner -> partner.getId().equals(partnerId))
                .orElse(false);
    }
}
