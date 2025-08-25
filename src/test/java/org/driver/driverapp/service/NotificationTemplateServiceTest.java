package org.driver.driverapp.service;

import org.driver.driverapp.dto.notification.request.CreateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.request.UpdateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationTemplateResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.NotificationTemplateMapper;
import org.driver.driverapp.model.NotificationTemplate;
import org.driver.driverapp.repository.NotificationTemplateRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceTest {

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @Mock
    private NotificationTemplateMapper notificationTemplateMapper;

    @InjectMocks
    private NotificationTemplateService notificationTemplateService;

    private NotificationTemplate testTemplate;
    private NotificationTemplateResponseDTO testTemplateResponseDTO;
    private CreateNotificationTemplateRequestDTO createRequestDTO;
    private UpdateNotificationTemplateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        testTemplate = NotificationTemplate.builder()
                .id(1L)
                .code("DELIVERY_ASSIGNED")
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .subject("Delivery Assignment")
                .body("Hello {name}, your delivery {deliveryId} has been assigned. ETA: {eta}")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testTemplateResponseDTO = NotificationTemplateResponseDTO.builder()
                .id(1L)
                .code("DELIVERY_ASSIGNED")
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .subject("Delivery Assignment")
                .body("Hello {name}, your delivery {deliveryId} has been assigned. ETA: {eta}")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        createRequestDTO = CreateNotificationTemplateRequestDTO.builder()
                .code("DELIVERY_ASSIGNED")
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .subject("Delivery Assignment")
                .body("Hello {name}, your delivery {deliveryId} has been assigned. ETA: {eta}")
                .build();

        updateRequestDTO = UpdateNotificationTemplateRequestDTO.builder()
                .code("DELIVERY_ASSIGNED")
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .subject("Updated Delivery Assignment")
                .body("Updated: Hello {name}, your delivery {deliveryId} has been assigned. ETA: {eta}")
                .build();
    }

    @Test
    void createTemplate_Success() {
        // Arrange
        when(notificationTemplateRepository.existsByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(false);
        when(notificationTemplateMapper.toEntity(createRequestDTO)).thenReturn(testTemplate);
        when(notificationTemplateRepository.save(testTemplate)).thenReturn(testTemplate);
        when(notificationTemplateMapper.toResponseDTO(testTemplate)).thenReturn(testTemplateResponseDTO);

        // Act
        NotificationTemplateResponseDTO result = notificationTemplateService.createTemplate(createRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateResponseDTO.getId(), result.getId());
        assertEquals(testTemplateResponseDTO.getCode(), result.getCode());
        verify(notificationTemplateRepository).save(testTemplate);
    }

    @Test
    void createTemplate_TemplateAlreadyExists_ThrowsException() {
        // Arrange
        when(notificationTemplateRepository.existsByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationTemplateService.createTemplate(createRequestDTO));

        verify(notificationTemplateRepository, never()).save(any(NotificationTemplate.class));
    }

    @Test
    void updateTemplate_Success() {
        // Arrange
        when(notificationTemplateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(notificationTemplateMapper.updateEntityFromDto(updateRequestDTO, testTemplate)).thenReturn(testTemplate);
        when(notificationTemplateRepository.save(testTemplate)).thenReturn(testTemplate);
        when(notificationTemplateMapper.toResponseDTO(testTemplate)).thenReturn(testTemplateResponseDTO);

        // Act
        NotificationTemplateResponseDTO result = notificationTemplateService.updateTemplate(1L, updateRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateResponseDTO.getId(), result.getId());
        verify(notificationTemplateRepository).save(testTemplate);
    }

    @Test
    void updateTemplate_TemplateNotFound_ThrowsException() {
        // Arrange
        when(notificationTemplateRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                notificationTemplateService.updateTemplate(999L, updateRequestDTO));

        verify(notificationTemplateRepository, never()).save(any(NotificationTemplate.class));
    }

    @Test
    void deleteTemplate_Success() {
        // Arrange
        when(notificationTemplateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(notificationTemplateRepository.save(testTemplate)).thenReturn(testTemplate);

        // Act
        notificationTemplateService.deleteTemplate(1L);

        // Assert
        assertFalse(testTemplate.isActive());
        verify(notificationTemplateRepository).save(testTemplate);
    }

    @Test
    void getTemplateById_Success() {
        // Arrange
        when(notificationTemplateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(notificationTemplateMapper.toResponseDTO(testTemplate)).thenReturn(testTemplateResponseDTO);

        // Act
        NotificationTemplateResponseDTO result = notificationTemplateService.getTemplateById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateResponseDTO.getId(), result.getId());
    }

    @Test
    void getTemplateById_NotFound_ThrowsException() {
        // Arrange
        when(notificationTemplateRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                notificationTemplateService.getTemplateById(999L));
    }

    @Test
    void getTemplateByCodeAndLanguage_Success() {
        // Arrange
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(Optional.of(testTemplate));
        when(notificationTemplateMapper.toResponseDTO(testTemplate)).thenReturn(testTemplateResponseDTO);

        // Act
        NotificationTemplateResponseDTO result = notificationTemplateService.getTemplateByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateResponseDTO.getId(), result.getId());
    }

    @Test
    void getTemplateByCodeAndLanguage_NotFound_ThrowsException() {
        // Arrange
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("INVALID_CODE", NotificationLanguage.ENGLISH))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                notificationTemplateService.getTemplateByCodeAndLanguage("INVALID_CODE", NotificationLanguage.ENGLISH));
    }

    @Test
    void getTemplatesByCode_Success() {
        // Arrange
        List<NotificationTemplate> templates = List.of(testTemplate);
        List<NotificationTemplateResponseDTO> responseDTOs = List.of(testTemplateResponseDTO);

        when(notificationTemplateRepository.findByCodeAndActiveTrue("DELIVERY_ASSIGNED")).thenReturn(templates);
        when(notificationTemplateMapper.toResponseDTOList(templates)).thenReturn(responseDTOs);

        // Act
        List<NotificationTemplateResponseDTO> result = notificationTemplateService.getTemplatesByCode("DELIVERY_ASSIGNED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTemplatesByType_Success() {
        // Arrange
        List<NotificationTemplate> templates = List.of(testTemplate);
        List<NotificationTemplateResponseDTO> responseDTOs = List.of(testTemplateResponseDTO);

        when(notificationTemplateRepository.findByTypeAndActiveTrue(NotificationType.SMS)).thenReturn(templates);
        when(notificationTemplateMapper.toResponseDTOList(templates)).thenReturn(responseDTOs);

        // Act
        List<NotificationTemplateResponseDTO> result = notificationTemplateService.getTemplatesByType(NotificationType.SMS);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTemplatesByLanguage_Success() {
        // Arrange
        List<NotificationTemplate> templates = List.of(testTemplate);
        List<NotificationTemplateResponseDTO> responseDTOs = List.of(testTemplateResponseDTO);

        when(notificationTemplateRepository.findByLanguageAndActiveTrue(NotificationLanguage.ENGLISH)).thenReturn(templates);
        when(notificationTemplateMapper.toResponseDTOList(templates)).thenReturn(responseDTOs);

        // Act
        List<NotificationTemplateResponseDTO> result = notificationTemplateService.getTemplatesByLanguage(NotificationLanguage.ENGLISH);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAllTemplates_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<NotificationTemplate> templatePage = new PageImpl<>(List.of(testTemplate));

        when(notificationTemplateRepository.findByActiveTrue(pageable)).thenReturn(templatePage);
        when(notificationTemplateMapper.toResponseDTO(testTemplate)).thenReturn(testTemplateResponseDTO);

        // Act
        Page<NotificationTemplateResponseDTO> result = notificationTemplateService.getAllTemplates(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testTemplateResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void searchTemplatesByCode_Success() {
        // Arrange
        List<NotificationTemplate> templates = List.of(testTemplate);
        List<NotificationTemplateResponseDTO> responseDTOs = List.of(testTemplateResponseDTO);

        when(notificationTemplateRepository.findByCodeContaining("DELIVERY")).thenReturn(templates);
        when(notificationTemplateMapper.toResponseDTOList(templates)).thenReturn(responseDTOs);

        // Act
        List<NotificationTemplateResponseDTO> result = notificationTemplateService.searchTemplatesByCode("DELIVERY");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void searchTemplatesByBody_Success() {
        // Arrange
        List<NotificationTemplate> templates = List.of(testTemplate);
        List<NotificationTemplateResponseDTO> responseDTOs = List.of(testTemplateResponseDTO);

        when(notificationTemplateRepository.findByBodyContaining("Hello")).thenReturn(templates);
        when(notificationTemplateMapper.toResponseDTOList(templates)).thenReturn(responseDTOs);

        // Act
        List<NotificationTemplateResponseDTO> result = notificationTemplateService.searchTemplatesByBody("Hello");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getTemplateEntityByCodeAndLanguage_Success() {
        // Arrange
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(Optional.of(testTemplate));

        // Act
        NotificationTemplate result = notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplate.getId(), result.getId());
    }

    @Test
    void getTemplateEntityByCodeAndLanguage_FallbackToEnglish_Success() {
        // Arrange
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.AMHARIC))
                .thenReturn(Optional.empty());
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(Optional.of(testTemplate));

        // Act
        NotificationTemplate result = notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.AMHARIC);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplate.getId(), result.getId());
    }

    @Test
    void getTemplateEntityByCodeAndLanguage_NoFallback_ThrowsException() {
        // Arrange
        when(notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue("INVALID_CODE", NotificationLanguage.ENGLISH))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                notificationTemplateService.getTemplateEntityByCodeAndLanguage("INVALID_CODE", NotificationLanguage.ENGLISH));
    }

    @Test
    void getTemplateCountByType_Success() {
        // Arrange
        when(notificationTemplateRepository.countByTypeAndActiveTrue(NotificationType.SMS)).thenReturn(5L);

        // Act
        long result = notificationTemplateService.getTemplateCountByType(NotificationType.SMS);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void getTemplateCountByLanguage_Success() {
        // Arrange
        when(notificationTemplateRepository.countByLanguageAndActiveTrue(NotificationLanguage.ENGLISH)).thenReturn(10L);

        // Act
        long result = notificationTemplateService.getTemplateCountByLanguage(NotificationLanguage.ENGLISH);

        // Assert
        assertEquals(10L, result);
    }

    @Test
    void getTemplateCountByCode_Success() {
        // Arrange
        when(notificationTemplateRepository.countByCodeAndActiveTrue("DELIVERY_ASSIGNED")).thenReturn(4L);

        // Act
        long result = notificationTemplateService.getTemplateCountByCode("DELIVERY_ASSIGNED");

        // Assert
        assertEquals(4L, result);
    }
}
