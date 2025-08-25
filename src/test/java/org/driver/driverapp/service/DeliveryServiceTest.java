package org.driver.driverapp.service;

import org.driver.driverapp.dto.delivery.request.ProofOfDeliveryDTO;
import org.driver.driverapp.dto.delivery.response.ProofOfDeliveryResponseDTO;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.enums.ProofOfDeliveryType;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private Delivery delivery;

    @InjectMocks
    private DeliveryService deliveryService;

    private MockMultipartFile mockFile;
    private ProofOfDeliveryDTO proofOfDeliveryDTO;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
            "file", 
            "test-proof.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        proofOfDeliveryDTO = ProofOfDeliveryDTO.builder()
                .deliveryId(1L)
                .proofOfDeliveryType(ProofOfDeliveryType.PHOTO)
                .file(mockFile)
                .deliveredLat(9.145)
                .deliveredLong(40.4897)
                .build();
    }

    @Test
    void uploadProofOfDelivery_Success() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(delivery.getStatus()).thenReturn(DeliveryStatus.IN_TRANSIT.name());
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(delivery.getId()).thenReturn(deliveryId);
        when(delivery.getProofOfDeliveryType()).thenReturn(ProofOfDeliveryType.PHOTO);
        when(delivery.getProofOfDeliveryUrl()).thenReturn("uploads/proof/test-file.jpg");
        when(delivery.getDeliveredAt()).thenReturn(OffsetDateTime.now());
        when(delivery.getDeliveredLat()).thenReturn(9.145);
        when(delivery.getDeliveredLong()).thenReturn(40.4897);

        // When
        ProofOfDeliveryResponseDTO result = deliveryService.uploadProofOfDelivery(deliveryId, proofOfDeliveryDTO);

        // Then
        assertNotNull(result);
        assertEquals(deliveryId, result.getDeliveryId());
        assertEquals(ProofOfDeliveryType.PHOTO, result.getProofOfDeliveryType());
        assertNotNull(result.getProofOfDeliveryUrl());
        assertNotNull(result.getDeliveredAt());
        assertEquals(9.145, result.getDeliveredLat());
        assertEquals(40.4897, result.getDeliveredLong());

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository).save(delivery);
        verify(delivery).setProofOfDeliveryType(ProofOfDeliveryType.PHOTO);
        verify(delivery).setProofOfDeliveryUrl(anyString());
        verify(delivery).setDeliveredAt(any(OffsetDateTime.class));
        verify(delivery).setDeliveredLat(9.145);
        verify(delivery).setDeliveredLong(40.4897);
    }

    @Test
    void uploadProofOfDelivery_DeliveryNotFound() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.uploadProofOfDelivery(deliveryId, proofOfDeliveryDTO);
        });

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void uploadProofOfDelivery_InvalidStatus() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(delivery.getStatus()).thenReturn(DeliveryStatus.DELIVERED.name());

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            deliveryService.uploadProofOfDelivery(deliveryId, proofOfDeliveryDTO);
        });

        verify(deliveryRepository).findById(deliveryId);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void getProofOfDelivery_Success() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(delivery.getId()).thenReturn(deliveryId);
        when(delivery.getProofOfDeliveryType()).thenReturn(ProofOfDeliveryType.SIGNATURE);
        when(delivery.getProofOfDeliveryUrl()).thenReturn("uploads/proof/signature.jpg");
        when(delivery.getDeliveredAt()).thenReturn(OffsetDateTime.now());
        when(delivery.getDeliveredLat()).thenReturn(9.145);
        when(delivery.getDeliveredLong()).thenReturn(40.4897);

        // When
        ProofOfDeliveryResponseDTO result = deliveryService.getProofOfDelivery(deliveryId);

        // Then
        assertNotNull(result);
        assertEquals(deliveryId, result.getDeliveryId());
        assertEquals(ProofOfDeliveryType.SIGNATURE, result.getProofOfDeliveryType());
        assertEquals("uploads/proof/signature.jpg", result.getProofOfDeliveryUrl());
        assertNotNull(result.getDeliveredAt());
        assertEquals(9.145, result.getDeliveredLat());
        assertEquals(40.4897, result.getDeliveredLong());

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void getProofOfDelivery_DeliveryNotFound() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.getProofOfDelivery(deliveryId);
        });

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void getProofOfDelivery_NoProofFound() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(delivery.getProofOfDeliveryUrl()).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.getProofOfDelivery(deliveryId);
        });

        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void uploadProofOfDelivery_ValidStatuses() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(delivery.getId()).thenReturn(deliveryId);
        when(delivery.getProofOfDeliveryType()).thenReturn(ProofOfDeliveryType.PHOTO);
        when(delivery.getProofOfDeliveryUrl()).thenReturn("uploads/proof/test.jpg");
        when(delivery.getDeliveredAt()).thenReturn(OffsetDateTime.now());

        // Test valid statuses
        String[] validStatuses = {
            DeliveryStatus.IN_PROGRESS.name(),
            DeliveryStatus.PICKED_UP.name(),
            DeliveryStatus.IN_TRANSIT.name()
        };

        for (String status : validStatuses) {
            when(delivery.getStatus()).thenReturn(status);

            // When
            ProofOfDeliveryResponseDTO result = deliveryService.uploadProofOfDelivery(deliveryId, proofOfDeliveryDTO);

            // Then
            assertNotNull(result);
            assertEquals(deliveryId, result.getDeliveryId());
        }
    }

    @Test
    void uploadProofOfDelivery_InvalidStatuses() {
        // Given
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        // Test invalid statuses
        String[] invalidStatuses = {
            DeliveryStatus.CREATED.name(),
            DeliveryStatus.PENDING.name(),
            DeliveryStatus.ACCEPTED.name(),
            DeliveryStatus.DELIVERED.name(),
            DeliveryStatus.CANCELED.name(),
            DeliveryStatus.DELAYED.name(),
            DeliveryStatus.DELIVERY_FAILED.name()
        };

        for (String status : invalidStatuses) {
            when(delivery.getStatus()).thenReturn(status);

            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                deliveryService.uploadProofOfDelivery(deliveryId, proofOfDeliveryDTO);
            });
        }
    }
}

