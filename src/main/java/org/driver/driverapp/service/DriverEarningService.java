package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.driver.response.DriverEarningResponseDTO;
import org.driver.driverapp.enums.PayoutStatus;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.DriverEarningMapper;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.model.Driver;
import org.driver.driverapp.model.DriverEarning;
import org.driver.driverapp.repository.DeliveryRepository;
import org.driver.driverapp.repository.DriverEarningRepository;
import org.driver.driverapp.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverEarningService {

    private final DriverEarningRepository driverEarningRepository;
    private final DriverRepository driverRepository;
    private final DeliveryRepository deliveryRepository;
    private final DriverEarningMapper driverEarningMapper;

    @Transactional
    public DriverEarningResponseDTO createEarning(Long driverId, Long deliveryId, BigDecimal amount, String description) {
        log.info("Creating earning for driver: {}, delivery: {}, amount: {}", driverId, deliveryId, amount);

        // Validate driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        // Validate delivery
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        // Check if earning already exists for this delivery
        Optional<DriverEarning> existingEarning = driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId);
        if (existingEarning.isPresent()) {
            throw new IllegalStateException("Earning already exists for delivery: " + deliveryId);
        }

        // Create earning
        DriverEarning earning = DriverEarning.builder()
                .driver(driver)
                .delivery(delivery)
                .amount(amount)
                .payoutStatus(PayoutStatus.PENDING)
                .description(description)
                .active(true)
                .build();

        earning = driverEarningRepository.save(earning);
        log.info("Earning created successfully: {}", earning.getId());

        return driverEarningMapper.toResponseDTO(earning);
    }

    @Transactional
    public DriverEarningResponseDTO processPayout(Long driverId) {
        log.info("Processing payout for driver: {}", driverId);

        // Get all pending earnings for the driver
        List<DriverEarning> pendingEarnings = driverEarningRepository.findByDriverIdAndPayoutStatusAndActiveTrue(
                driverId, PayoutStatus.PENDING);

        if (pendingEarnings.isEmpty()) {
            throw new IllegalStateException("No pending earnings found for driver: " + driverId);
        }

        BigDecimal totalAmount = pendingEarnings.stream()
                .map(DriverEarning::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Processing payout of {} ETB for driver: {}", totalAmount, driverId);

        // Process each earning
        for (DriverEarning earning : pendingEarnings) {
            earning.markAsProcessing();
            driverEarningRepository.save(earning);
        }

        // Simulate payout processing
        try {
            Thread.sleep(100); // Simulate processing time
            
            // Simulate 95% success rate
            boolean success = Math.random() > 0.05;
            
            if (success) {
                String payoutReference = "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                
                for (DriverEarning earning : pendingEarnings) {
                    earning.markAsCompleted(payoutReference);
                    driverEarningRepository.save(earning);
                }
                
                log.info("Payout processed successfully for driver: {}", driverId);
            } else {
                for (DriverEarning earning : pendingEarnings) {
                    earning.markAsFailed("Payout processing failed");
                    driverEarningRepository.save(earning);
                }
                
                log.error("Payout processing failed for driver: {}", driverId);
            }
        } catch (Exception e) {
            log.error("Error processing payout for driver: {}", driverId, e);
            for (DriverEarning earning : pendingEarnings) {
                earning.markAsFailed("Payout processing error: " + e.getMessage());
                driverEarningRepository.save(earning);
            }
        }

        // Return the first earning as representative
        return driverEarningMapper.toResponseDTO(pendingEarnings.get(0));
    }

    @Transactional(readOnly = true)
    public DriverEarningResponseDTO getEarningById(Long id) {
        DriverEarning earning = driverEarningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver earning not found with id: " + id));
        return driverEarningMapper.toResponseDTO(earning);
    }

    @Transactional(readOnly = true)
    public Page<DriverEarningResponseDTO> getEarningsByDriver(Long driverId, Pageable pageable) {
        Page<DriverEarning> earnings = driverEarningRepository.findByDriverIdAndActiveTrue(driverId, pageable);
        return earnings.map(driverEarningMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<DriverEarningResponseDTO> getEarningsByPayoutStatus(PayoutStatus payoutStatus, Pageable pageable) {
        Page<DriverEarning> earnings = driverEarningRepository.findByPayoutStatusAndActiveTrue(payoutStatus, pageable);
        return earnings.map(driverEarningMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public DriverEarningResponseDTO getEarningByDelivery(Long deliveryId) {
        DriverEarning earning = driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver earning not found for delivery: " + deliveryId));
        return driverEarningMapper.toResponseDTO(earning);
    }

    @Transactional(readOnly = true)
    public List<DriverEarningResponseDTO> getPendingPayouts() {
        List<DriverEarning> earnings = driverEarningRepository.findPendingPayouts();
        return driverEarningMapper.toResponseDTOList(earnings);
    }

    @Transactional(readOnly = true)
    public List<DriverEarningResponseDTO> getFailedPayouts() {
        List<DriverEarning> earnings = driverEarningRepository.findFailedPayouts();
        return driverEarningMapper.toResponseDTOList(earnings);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEarningsByDriver(Long driverId) {
        return driverEarningRepository.sumAmountByDriverId(driverId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPendingEarningsByDriver(Long driverId) {
        return driverEarningRepository.sumAmountByDriverIdAndPayoutStatus(driverId, PayoutStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEarningsByPayoutStatus(PayoutStatus payoutStatus) {
        return driverEarningRepository.sumAmountByPayoutStatus(payoutStatus);
    }

    @Transactional(readOnly = true)
    public List<DriverEarningResponseDTO> getEarningsByDateRange(Instant startDate, Instant endDate) {
        List<DriverEarning> earnings = driverEarningRepository.findByCreatedAtBetween(startDate, endDate);
        return driverEarningMapper.toResponseDTOList(earnings);
    }

    @Transactional(readOnly = true)
    public List<DriverEarningResponseDTO> getEarningsByDriverAndDateRange(Long driverId, Instant startDate, Instant endDate) {
        List<DriverEarning> earnings = driverEarningRepository.findByDriverIdAndCreatedAtBetween(driverId, startDate, endDate);
        return driverEarningMapper.toResponseDTOList(earnings);
    }

    @Transactional
    public DriverEarningResponseDTO createEarningFromDelivery(Long deliveryId) {
        log.info("Creating earning from delivery: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getDriver() == null) {
            throw new IllegalStateException("Delivery has no assigned driver: " + deliveryId);
        }

        // Calculate driver earnings (typically 70-80% of delivery price)
        BigDecimal deliveryPrice = BigDecimal.valueOf(delivery.getPrice());
        BigDecimal driverEarning = deliveryPrice.multiply(BigDecimal.valueOf(0.75)); // 75% commission

        String description = "Earning from delivery: " + delivery.getDeliveryCode();

        return createEarning(delivery.getDriver().getId(), deliveryId, driverEarning, description);
    }

    @Transactional
    public void processAllPendingPayouts() {
        log.info("Processing all pending payouts");
        List<DriverEarning> pendingEarnings = driverEarningRepository.findPendingPayouts();
        
        // Group by driver
        Map<Long, List<DriverEarning>> earningsByDriver = pendingEarnings.stream()
                .collect(java.util.stream.Collectors.groupingBy(earning -> earning.getDriver().getId()));
        
        for (Map.Entry<Long, List<DriverEarning>> entry : earningsByDriver.entrySet()) {
            try {
                processPayout(entry.getKey());
            } catch (Exception e) {
                log.error("Error processing payout for driver: {}", entry.getKey(), e);
            }
        }
        
        log.info("Processed payouts for {} drivers", earningsByDriver.size());
    }
}
