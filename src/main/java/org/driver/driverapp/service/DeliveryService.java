package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import org.driver.driverapp.dto.CreateDeliveryRequestDTO;
import org.driver.driverapp.dto.DeliveryResponseDTO;
import org.driver.driverapp.mapper.DeliveryMapper;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PartnerRepository partnerRepository;
    private final DriverRepository driverRepository;
    private final DeliveryMapper deliveryMapper;

    public DeliveryResponseDTO createDelivery(CreateDeliveryRequestDTO dto) {
        Partner pickupPartner = partnerRepository.findById(dto.getPickupPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Pickup partner not found"));

        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        Delivery delivery = deliveryMapper.fromCreateDTO(dto, pickupPartner, driver);
        return deliveryMapper.toDTO(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponseDTO> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream()
                .map(deliveryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<DeliveryResponseDTO> getDeliveryById(Long id) {
        return deliveryRepository.findById(id).map(deliveryMapper::toDTO);
    }

    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }
}
