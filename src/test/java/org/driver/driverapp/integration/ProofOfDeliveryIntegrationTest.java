package org.driver.driverapp.integration;

import org.driver.driverapp.config.TestConfig;
import org.driver.driverapp.dto.delivery.response.ProofOfDeliveryResponseDTO;
import org.driver.driverapp.enums.DeliveryStatus;
import org.driver.driverapp.enums.ProofOfDeliveryType;
import org.driver.driverapp.model.Delivery;
import org.driver.driverapp.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.OffsetDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class ProofOfDeliveryIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DeliveryRepository deliveryRepository;

    private MockMvc mockMvc;
    private Delivery testDelivery;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create a test delivery
        testDelivery = Delivery.builder()
                .deliveryCode("TEST-DEL-001")
                .status(DeliveryStatus.IN_TRANSIT.name())
                .dropoffAddress("Test Address, Addis Ababa")
                .createdAt(OffsetDateTime.now().toInstant())
                .build();
        testDelivery = deliveryRepository.save(testDelivery);
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO")
                        .param("deliveredLat", "9.145")
                        .param("deliveredLong", "40.4897"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(testDelivery.getId()))
                .andExpect(jsonPath("$.proofOfDeliveryType").value("PHOTO"))
                .andExpect(jsonPath("$.proofOfDeliveryUrl").exists())
                .andExpect(jsonPath("$.deliveredAt").exists())
                .andExpect(jsonPath("$.deliveredLat").value(9.145))
                .andExpect(jsonPath("$.deliveredLong").value(40.4897));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_WithSignature() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "signature.png",
                "image/png",
                "signature image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "SIGNATURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(testDelivery.getId()))
                .andExpect(jsonPath("$.proofOfDeliveryType").value("SIGNATURE"))
                .andExpect(jsonPath("$.proofOfDeliveryUrl").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_DeliveryNotFound() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", 99999L)
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_InvalidStatus() throws Exception {
        // Given
        testDelivery.setStatus(DeliveryStatus.DELIVERED.name());
        deliveryRepository.save(testDelivery);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_MissingFile() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_MissingType() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getProofOfDelivery_Success() throws Exception {
        // Given - First upload proof
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isOk());

        // When & Then - Get proof
        mockMvc.perform(get("/api/deliveries/{id}/proof", testDelivery.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value(testDelivery.getId()))
                .andExpect(jsonPath("$.proofOfDeliveryType").value("PHOTO"))
                .andExpect(jsonPath("$.proofOfDeliveryUrl").exists())
                .andExpect(jsonPath("$.deliveredAt").exists());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getProofOfDelivery_NoProofFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/deliveries/{id}/proof", testDelivery.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getProofOfDelivery_DeliveryNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/deliveries/{id}/proof", 99999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadProofOfDelivery_AdminAccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void uploadProofOfDelivery_PartnerAccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void uploadProofOfDelivery_CustomerAccessDenied() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadProofOfDelivery_Unauthorized() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                        .file(file)
                        .param("proofOfDeliveryType", "PHOTO"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_ValidStatuses() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Test all valid statuses
        String[] validStatuses = {
                DeliveryStatus.IN_PROGRESS.name(),
                DeliveryStatus.PICKED_UP.name(),
                DeliveryStatus.IN_TRANSIT.name()
        };

        for (String status : validStatuses) {
            testDelivery.setStatus(status);
            deliveryRepository.save(testDelivery);

            // When & Then
            mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                            .file(file)
                            .param("proofOfDeliveryType", "PHOTO"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void uploadProofOfDelivery_InvalidStatuses() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

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
            testDelivery.setStatus(status);
            deliveryRepository.save(testDelivery);

            // When & Then
            mockMvc.perform(multipart("/api/deliveries/{id}/proof", testDelivery.getId())
                            .file(file)
                            .param("proofOfDeliveryType", "PHOTO"))
                    .andExpect(status().isInternalServerError());
        }
    }
}

