package org.driver.driverapp.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverEarningServiceTest {

    @Mock
    private DriverEarningRepository driverEarningRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DriverEarningMapper driverEarningMapper;

    @InjectMocks
    private DriverEarningService driverEarningService;

    private Driver testDriver;
    private Delivery testDelivery;
    private DriverEarning testDriverEarning;
    private DriverEarningResponseDTO testDriverEarningResponseDTO;

    @BeforeEach
    void setUp() {
        testDriver = Driver.builder()
                .id(1L)
                .name("Test Driver")
                .phoneNumber("+251912345678")
                .build();

        testDelivery = Delivery.builder()
                .id(1L)
                .deliveryCode("DEL-001")
                .price(200.0)
                .driver(testDriver)
                .build();

        testDriverEarning = DriverEarning.builder()
                .id(1L)
                .driver(testDriver)
                .delivery(testDelivery)
                .amount(BigDecimal.valueOf(150.00))
                .payoutStatus(PayoutStatus.PENDING)
                .description("Earning from delivery: DEL-001")
                .active(true)
                .build();

        testDriverEarningResponseDTO = DriverEarningResponseDTO.builder()
                .id(1L)
                .driverId(1L)
                .driverName("Test Driver")
                .deliveryId(1L)
                .deliveryCode("DEL-001")
                .amount(BigDecimal.valueOf(150.00))
                .payoutStatus(PayoutStatus.PENDING)
                .description("Earning from delivery: DEL-001")
                .build();
    }

    @Test
    void createEarning_Success() {
        // Arrange
        Long driverId = 1L;
        Long deliveryId = 1L;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String description = "Test earning";

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(testDriver));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(testDelivery));
        when(driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId))
                .thenReturn(Optional.empty());
        when(driverEarningRepository.save(any(DriverEarning.class))).thenReturn(testDriverEarning);
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        DriverEarningResponseDTO result = driverEarningService.createEarning(driverId, deliveryId, amount, description);

        // Assert
        assertNotNull(result);
        assertEquals(testDriverEarningResponseDTO.getId(), result.getId());
        assertEquals(testDriverEarningResponseDTO.getDriverId(), result.getDriverId());
        assertEquals(testDriverEarningResponseDTO.getDeliveryId(), result.getDeliveryId());

        verify(driverEarningRepository).save(any(DriverEarning.class));
    }

    @Test
    void createEarning_DriverNotFound() {
        // Arrange
        Long driverId = 999L;
        Long deliveryId = 1L;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String description = "Test earning";

        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                driverEarningService.createEarning(driverId, deliveryId, amount, description));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void createEarning_DeliveryNotFound() {
        // Arrange
        Long driverId = 1L;
        Long deliveryId = 999L;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String description = "Test earning";

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(testDriver));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                driverEarningService.createEarning(driverId, deliveryId, amount, description));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void createEarning_EarningAlreadyExists() {
        // Arrange
        Long driverId = 1L;
        Long deliveryId = 1L;
        BigDecimal amount = BigDecimal.valueOf(150.00);
        String description = "Test earning";

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(testDriver));
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(testDelivery));
        when(driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId))
                .thenReturn(Optional.of(testDriverEarning));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                driverEarningService.createEarning(driverId, deliveryId, amount, description));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void processPayout_Success() {
        // Arrange
        Long driverId = 1L;
        List<DriverEarning> pendingEarnings = List.of(testDriverEarning);

        when(driverEarningRepository.findByDriverIdAndPayoutStatusAndActiveTrue(driverId, PayoutStatus.PENDING))
                .thenReturn(pendingEarnings);
        when(driverEarningRepository.save(any(DriverEarning.class))).thenReturn(testDriverEarning);
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        DriverEarningResponseDTO result = driverEarningService.processPayout(driverId);

        // Assert
        assertNotNull(result);
        assertEquals(testDriverEarningResponseDTO.getId(), result.getId());

        verify(driverEarningRepository, times(2)).save(any(DriverEarning.class));
    }

    @Test
    void processPayout_NoPendingEarnings() {
        // Arrange
        Long driverId = 1L;

        when(driverEarningRepository.findByDriverIdAndPayoutStatusAndActiveTrue(driverId, PayoutStatus.PENDING))
                .thenReturn(List.of());

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                driverEarningService.processPayout(driverId));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void getEarningById_Success() {
        // Arrange
        Long earningId = 1L;

        when(driverEarningRepository.findById(earningId)).thenReturn(Optional.of(testDriverEarning));
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        DriverEarningResponseDTO result = driverEarningService.getEarningById(earningId);

        // Assert
        assertNotNull(result);
        assertEquals(testDriverEarningResponseDTO.getId(), result.getId());
    }

    @Test
    void getEarningById_NotFound() {
        // Arrange
        Long earningId = 999L;

        when(driverEarningRepository.findById(earningId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                driverEarningService.getEarningById(earningId));
    }

    @Test
    void getEarningsByDriver_Success() {
        // Arrange
        Long driverId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Page<DriverEarning> earningPage = new PageImpl<>(List.of(testDriverEarning));

        when(driverEarningRepository.findByDriverIdAndActiveTrue(driverId, pageable)).thenReturn(earningPage);
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        Page<DriverEarningResponseDTO> result = driverEarningService.getEarningsByDriver(driverId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getEarningByDelivery_Success() {
        // Arrange
        Long deliveryId = 1L;

        when(driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId))
                .thenReturn(Optional.of(testDriverEarning));
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        DriverEarningResponseDTO result = driverEarningService.getEarningByDelivery(deliveryId);

        // Assert
        assertNotNull(result);
        assertEquals(testDriverEarningResponseDTO.getId(), result.getId());
    }

    @Test
    void getEarningByDelivery_NotFound() {
        // Arrange
        Long deliveryId = 999L;

        when(driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                driverEarningService.getEarningByDelivery(deliveryId));
    }

    @Test
    void getEarningsByPayoutStatus_Success() {
        // Arrange
        PayoutStatus payoutStatus = PayoutStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 20);
        Page<DriverEarning> earningPage = new PageImpl<>(List.of(testDriverEarning));

        when(driverEarningRepository.findByPayoutStatusAndActiveTrue(payoutStatus, pageable)).thenReturn(earningPage);
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        Page<DriverEarningResponseDTO> result = driverEarningService.getEarningsByPayoutStatus(payoutStatus, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getPendingPayouts_Success() {
        // Arrange
        List<DriverEarning> pendingEarnings = List.of(testDriverEarning);

        when(driverEarningRepository.findPendingPayouts()).thenReturn(pendingEarnings);
        when(driverEarningMapper.toResponseDTOList(pendingEarnings)).thenReturn(List.of(testDriverEarningResponseDTO));

        // Act
        List<DriverEarningResponseDTO> result = driverEarningService.getPendingPayouts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getFailedPayouts_Success() {
        // Arrange
        List<DriverEarning> failedEarnings = List.of(testDriverEarning);

        when(driverEarningRepository.findFailedPayouts()).thenReturn(failedEarnings);
        when(driverEarningMapper.toResponseDTOList(failedEarnings)).thenReturn(List.of(testDriverEarningResponseDTO));

        // Act
        List<DriverEarningResponseDTO> result = driverEarningService.getFailedPayouts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTotalEarningsByDriver_Success() {
        // Arrange
        Long driverId = 1L;
        BigDecimal expectedTotal = BigDecimal.valueOf(1500.00);

        when(driverEarningRepository.sumAmountByDriverId(driverId)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = driverEarningService.getTotalEarningsByDriver(driverId);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getPendingEarningsByDriver_Success() {
        // Arrange
        Long driverId = 1L;
        BigDecimal expectedTotal = BigDecimal.valueOf(500.00);

        when(driverEarningRepository.sumAmountByDriverIdAndPayoutStatus(driverId, PayoutStatus.PENDING))
                .thenReturn(expectedTotal);

        // Act
        BigDecimal result = driverEarningService.getPendingEarningsByDriver(driverId);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getTotalEarningsByPayoutStatus_Success() {
        // Arrange
        PayoutStatus payoutStatus = PayoutStatus.COMPLETED;
        BigDecimal expectedTotal = BigDecimal.valueOf(3000.00);

        when(driverEarningRepository.sumAmountByPayoutStatus(payoutStatus)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = driverEarningService.getTotalEarningsByPayoutStatus(payoutStatus);

        // Assert
        assertEquals(expectedTotal, result);
    }

    @Test
    void getEarningsByDateRange_Success() {
        // Arrange
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<DriverEarning> earnings = List.of(testDriverEarning);

        when(driverEarningRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(earnings);
        when(driverEarningMapper.toResponseDTOList(earnings)).thenReturn(List.of(testDriverEarningResponseDTO));

        // Act
        List<DriverEarningResponseDTO> result = driverEarningService.getEarningsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getEarningsByDriverAndDateRange_Success() {
        // Arrange
        Long driverId = 1L;
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<DriverEarning> earnings = List.of(testDriverEarning);

        when(driverEarningRepository.findByDriverIdAndCreatedAtBetween(driverId, startDate, endDate))
                .thenReturn(earnings);
        when(driverEarningMapper.toResponseDTOList(earnings)).thenReturn(List.of(testDriverEarningResponseDTO));

        // Act
        List<DriverEarningResponseDTO> result = driverEarningService.getEarningsByDriverAndDateRange(driverId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDriverEarningResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void createEarningFromDelivery_Success() {
        // Arrange
        Long deliveryId = 1L;

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(testDelivery));
        when(driverRepository.findById(testDriver.getId())).thenReturn(Optional.of(testDriver));
        when(driverEarningRepository.findByDeliveryIdAndActiveTrue(deliveryId))
                .thenReturn(Optional.empty());
        when(driverEarningRepository.save(any(DriverEarning.class))).thenReturn(testDriverEarning);
        when(driverEarningMapper.toResponseDTO(testDriverEarning)).thenReturn(testDriverEarningResponseDTO);

        // Act
        DriverEarningResponseDTO result = driverEarningService.createEarningFromDelivery(deliveryId);

        // Assert
        assertNotNull(result);
        assertEquals(testDriverEarningResponseDTO.getId(), result.getId());

        verify(driverEarningRepository).save(any(DriverEarning.class));
    }

    @Test
    void createEarningFromDelivery_DeliveryNotFound() {
        // Arrange
        Long deliveryId = 999L;

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                driverEarningService.createEarningFromDelivery(deliveryId));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void createEarningFromDelivery_NoDriverAssigned() {
        // Arrange
        Long deliveryId = 1L;
        Delivery deliveryWithoutDriver = Delivery.builder()
                .id(1L)
                .deliveryCode("DEL-001")
                .price(200.0)
                .driver(null)
                .build();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(deliveryWithoutDriver));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                driverEarningService.createEarningFromDelivery(deliveryId));

        verify(driverEarningRepository, never()).save(any(DriverEarning.class));
    }

    @Test
    void processAllPendingPayouts_Success() {
        // Arrange
        List<DriverEarning> pendingEarnings = List.of(testDriverEarning);

        when(driverEarningRepository.findPendingPayouts()).thenReturn(pendingEarnings);
        when(driverEarningRepository.findByDriverIdAndPayoutStatusAndActiveTrue(testDriver.getId(), PayoutStatus.PENDING))
                .thenReturn(pendingEarnings);
        when(driverEarningRepository.save(any(DriverEarning.class))).thenReturn(testDriverEarning);

        // Act
        driverEarningService.processAllPendingPayouts();

        // Assert
        verify(driverEarningRepository, times(2)).save(any(DriverEarning.class));
    }
}
