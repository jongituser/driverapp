package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.*;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponseDTO toDTO(Delivery delivery) {
        return DeliveryResponseDTO.builder()
                .id(delivery.getId())
                .pickupPartner(PartnerDTO.builder()
                        .id(delivery.getPickupPartner().getId())
                        .name(delivery.getPickupPartner().getName())
                        .address(delivery.getPickupPartner().getAddress())
                        .phone(delivery.getPickupPartner().getPhone())
                        .email(delivery.getPickupPartner().getEmail())
                        .build())
                .drop(delivery.getDropoffAddress())
                .status(delivery.getStatus())
                .createdAt(delivery.getCreatedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .build();
    }

    public Delivery fromCreateDTO(CreateDeliveryRequestDTO dto, Partner partner, Driver driver) {
        return Delivery.builder()
                .pickupPartner(partner)
                .dropoffAddress(dto.getDropoffAddress())
                .status(dto.getStatus())
                .driver(driver)
                .build();
    }
}
