package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.delivery.request.CreateDeliveryRequestDTO;
import org.driver.driverapp.dto.delivery.request.ProofOfDeliveryDTO;
import org.driver.driverapp.dto.delivery.response.DeliveryResponseDTO;
import org.driver.driverapp.dto.delivery.response.ProofOfDeliveryResponseDTO;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.mapper.DeliveryMapper;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.Partner;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.driver.driverapp.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    /**
     * Upload proof of delivery for a delivery
     * Business rule: A delivery cannot transition to COMPLETED without proof
     */
    public ProofOfDeliveryResponseDTO uploadProofOfDelivery(Long deliveryId, ProofOfDeliveryDTO dto) {
        log.info("Uploading proof of delivery for delivery: {}", deliveryId);
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
        
        // Validate that delivery is in a state that allows POD upload
        if (!canUploadProofOfDelivery(delivery)) {
            throw new IllegalStateException("Delivery is not in a state that allows proof of delivery upload");
        }
        
        // Save the file
        String filePath = saveProofFile(dto.getFile());
        
        // Update delivery with proof details
        delivery.setProofOfDeliveryType(dto.getProofOfDeliveryType());
        delivery.setProofOfDeliveryUrl(filePath);
        delivery.setDeliveredAt(OffsetDateTime.now(ZoneOffset.UTC));
        delivery.setDeliveredLat(dto.getDeliveredLat());
        delivery.setDeliveredLong(dto.getDeliveredLong());
        
        // Note: Status is not automatically changed to COMPLETED here
        // This allows for manual verification before completion
        
        Delivery savedDelivery = deliveryRepository.save(delivery);
        
        return ProofOfDeliveryResponseDTO.builder()
                .deliveryId(savedDelivery.getId())
                .proofOfDeliveryType(savedDelivery.getProofOfDeliveryType())
                .proofOfDeliveryUrl(savedDelivery.getProofOfDeliveryUrl())
                .deliveredAt(savedDelivery.getDeliveredAt())
                .deliveredLat(savedDelivery.getDeliveredLat())
                .deliveredLong(savedDelivery.getDeliveredLong())
                .build();
    }
    
    /**
     * Get proof of delivery details for a delivery
     */
    public ProofOfDeliveryResponseDTO getProofOfDelivery(Long deliveryId) {
        log.info("Getting proof of delivery for delivery: {}", deliveryId);
        
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
        
        if (delivery.getProofOfDeliveryUrl() == null) {
            throw new IllegalArgumentException("No proof of delivery found for this delivery");
        }
        
        return ProofOfDeliveryResponseDTO.builder()
                .deliveryId(delivery.getId())
                .proofOfDeliveryType(delivery.getProofOfDeliveryType())
                .proofOfDeliveryUrl(delivery.getProofOfDeliveryUrl())
                .deliveredAt(delivery.getDeliveredAt())
                .deliveredLat(delivery.getDeliveredLat())
                .deliveredLong(delivery.getDeliveredLong())
                .build();
    }
    
    /**
     * Check if a delivery can have proof of delivery uploaded
     */
    private boolean canUploadProofOfDelivery(Delivery delivery) {
        // Can upload POD if delivery is in progress, picked up, or in transit
        String status = delivery.getStatus();
        return DeliveryStatus.IN_PROGRESS.name().equals(status) ||
               DeliveryStatus.PICKED_UP.name().equals(status) ||
               DeliveryStatus.IN_TRANSIT.name().equals(status);
    }
    
    /**
     * Save proof file to local storage (stubbed implementation)
     */
    private String saveProofFile(MultipartFile file) {
        try {
            // Create uploads directory if it doesn't exist
            Path uploadsDir = Paths.get("uploads", "proof");
            Files.createDirectories(uploadsDir);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadsDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            log.info("Proof file saved to: {}", filePath);
            return filePath.toString();
            
        } catch (IOException e) {
            log.error("Failed to save proof file", e);
            throw new RuntimeException("Failed to save proof file", e);
        }
    }
}
