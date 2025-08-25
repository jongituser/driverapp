package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.partner.PartnerDTO;
import org.driver.driverapp.dto.delivery.request.CreateDeliveryRequestDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryResponseDTO;
import org.driver.driverapp.dto.driver.DriverResponseDTO;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DeliveryMapper {

	public DeliveryResponseDTO toDTO(Delivery delivery) {
		PartnerDTO pickup = null;
		if (delivery.getPickupPartner() != null) {
			pickup = PartnerDTO.builder()
					.id(delivery.getPickupPartner().getId())
					.name(delivery.getPickupPartner().getName())
					.address(delivery.getPickupPartner().getAddress())
					.phone(delivery.getPickupPartner().getPhone())
					.email(delivery.getPickupPartner().getEmail())
					.build();
		}

		PartnerDTO dropoff = null;
		if (delivery.getDropoffPartner() != null) {
			dropoff = PartnerDTO.builder()
					.id(delivery.getDropoffPartner().getId())
					.name(delivery.getDropoffPartner().getName())
					.address(delivery.getDropoffPartner().getAddress())
					.phone(delivery.getDropoffPartner().getPhone())
					.email(delivery.getDropoffPartner().getEmail())
					.build();
		}

		DriverResponseDTO driverDto = null;
		Driver driver = delivery.getDriver();
		if (driver != null) {
			driverDto = DriverResponseDTO.builder()
					.id(driver.getId())
					.name(driver.getName())
					.phoneNumber(driver.getPhoneNumber())
					.email(driver.getEmail())
					.licenseNumber(driver.getLicenseNumber())
					.vehicleType(driver.getVehicleType())
					.vehiclePlateNumber(driver.getVehiclePlateNumber())
					.vehicleColor(driver.getVehicleColor())
					.profileImageUrl(driver.getProfileImageUrl())
					.status(driver.getStatus())
					.isOnline(driver.isOnline())
					.totalDeliveries(driver.getTotalDeliveries())
					.activeDeliveries(driver.getActiveDeliveries())
					.build();
		}

		boolean delivered = delivery.getDropoffTime() != null
				|| (delivery.getStatus() != null && delivery.getStatus().equalsIgnoreCase("DELIVERED"));

		OffsetDateTime createdAt = delivery.getCreatedAt() != null
				? delivery.getCreatedAt().atOffset(ZoneOffset.UTC)
				: null;
		OffsetDateTime deliveredAt = delivery.getDropoffTime();

		return DeliveryResponseDTO.builder()
				.id(delivery.getId())
				.pickupPartner(pickup)
				.dropoffPartner(dropoff)
				.driver(driverDto)
				.createdAt(createdAt)
				.deliveredAt(deliveredAt)
				.delivered(delivered)
				.build();
	}

	public Delivery fromCreateDTO(CreateDeliveryRequestDTO dto, Partner partner, Driver driver) {
		return Delivery.builder()
				.pickupPartner(partner)
				.dropoffAddress(dto.getDropoffAddress())
				.status(dto.getStatus() != null ? dto.getStatus().name() : null)
				.driver(driver)
				.build();
	}
}
