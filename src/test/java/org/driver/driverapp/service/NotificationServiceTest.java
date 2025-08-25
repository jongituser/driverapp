package org.driver.driverapp.service;

import org.driver.driverapp.dto.notification.request.SendNotificationRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationStatus;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.NotificationMapper;
import org.driver.driverapp.model.Notification;
import org.driver.driverapp.model.NotificationTemplate;
import org.driver.driverapp.repository.NotificationRepository;
import org.driver.driverapp.service.notification.EmailService;
import org.driver.driverapp.service.notification.PushService;
import org.driver.driverapp.service.notification.SmsService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTemplateService notificationTemplateService;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private SmsService smsService;

    @Mock
    private EmailService emailService;

    @Mock
    private PushService pushService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationTemplate testTemplate;
    private Notification testNotification;
    private NotificationResponseDTO testNotificationResponseDTO;
    private SendNotificationRequestDTO sendRequestDTO;

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

        testNotification = Notification.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .templateId(1L)
                .status(NotificationStatus.PENDING)
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testNotificationResponseDTO = NotificationResponseDTO.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.SMS)
                .language(NotificationLanguage.ENGLISH)
                .templateId(1L)
                .status(NotificationStatus.SENT)
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        placeholders.put("deliveryId", "123");
        placeholders.put("eta", "30 minutes");

        sendRequestDTO = SendNotificationRequestDTO.builder()
                .recipientId(1L)
                .templateCode("DELIVERY_ASSIGNED")
                .language(NotificationLanguage.ENGLISH)
                .placeholders(placeholders)
                .build();
    }

    @Test
    void sendNotification_SmsSuccess() {
        // Arrange
        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(smsService.sendSms(anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
        assertTrue(testNotification.isSent());
        verify(smsService).sendSms(anyString(), contains("Hello John, your delivery 123 has been assigned. ETA: 30 minutes"));
    }

    @Test
    void sendNotification_EmailSuccess() {
        // Arrange
        testTemplate.setType(NotificationType.EMAIL);
        sendRequestDTO.setTemplateCode("DELIVERY_ASSIGNED");

        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
        assertTrue(testNotification.isSent());
        verify(emailService).sendEmail(anyString(), eq("Delivery Assignment"), contains("Hello John, your delivery 123 has been assigned. ETA: 30 minutes"));
    }

    @Test
    void sendNotification_PushSuccess() {
        // Arrange
        testTemplate.setType(NotificationType.PUSH);
        sendRequestDTO.setTemplateCode("DELIVERY_ASSIGNED");

        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(pushService.sendPushNotificationWithData(anyString(), anyString(), anyString(), anyMap())).thenReturn(true);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
        assertTrue(testNotification.isSent());
        verify(pushService).sendPushNotificationWithData(anyString(), eq("Delivery Assignment"), contains("Hello John, your delivery 123 has been assigned. ETA: 30 minutes"), anyMap());
    }

    @Test
    void sendNotification_SmsFailure() {
        // Arrange
        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(smsService.sendSms(anyString(), anyString())).thenReturn(false);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(testNotification.isFailed());
        assertEquals("Failed to send notification", testNotification.getErrorMessage());
    }

    @Test
    void sendNotification_TemplateNotFound_ThrowsException() {
        // Arrange
        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("INVALID_CODE", NotificationLanguage.ENGLISH))
                .thenThrow(new ResourceNotFoundException("Template not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                notificationService.sendNotification(sendRequestDTO));

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void sendNotification_UnsupportedType_ThrowsException() {
        // Arrange
        testTemplate.setType(null);
        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                notificationService.sendNotification(sendRequestDTO));
    }

    @Test
    void getNotificationById_Success() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.getNotificationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
    }

    @Test
    void getNotificationById_NotFound_ThrowsException() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                notificationService.getNotificationById(999L));
    }

    @Test
    void getNotificationsByRecipient_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByRecipientIdAndActiveTrue(1L)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByRecipient(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getNotificationsByRecipient_WithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification));

        when(notificationRepository.findByRecipientIdAndActiveTrue(1L, pageable)).thenReturn(notificationPage);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        Page<NotificationResponseDTO> result = notificationService.getNotificationsByRecipient(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testNotificationResponseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getNotificationsByStatus_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByStatusAndActiveTrue(NotificationStatus.PENDING)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByStatus(NotificationStatus.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getNotificationsByType_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByTypeAndActiveTrue(NotificationType.SMS)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByType(NotificationType.SMS);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getNotificationsByLanguage_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByLanguageAndActiveTrue(NotificationLanguage.ENGLISH)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByLanguage(NotificationLanguage.ENGLISH);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getPendingNotifications_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByStatusAndActiveTrueOrderByCreatedAtAsc(NotificationStatus.PENDING)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getPendingNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getFailedNotifications_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        List<NotificationResponseDTO> responseDTOs = List.of(testNotificationResponseDTO);

        when(notificationRepository.findByStatusAndActiveTrueOrderByCreatedAtDesc(NotificationStatus.FAILED)).thenReturn(notifications);
        when(notificationMapper.toResponseDTOList(notifications)).thenReturn(responseDTOs);

        // Act
        List<NotificationResponseDTO> result = notificationService.getFailedNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotificationResponseDTO.getId(), result.get(0).getId());
    }

    @Test
    void getNotificationCountByRecipient_Success() {
        // Arrange
        when(notificationRepository.countByRecipientIdAndActiveTrue(1L)).thenReturn(5L);

        // Act
        long result = notificationService.getNotificationCountByRecipient(1L);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void getNotificationCountByStatus_Success() {
        // Arrange
        when(notificationRepository.countByStatusAndActiveTrue(NotificationStatus.SENT)).thenReturn(10L);

        // Act
        long result = notificationService.getNotificationCountByStatus(NotificationStatus.SENT);

        // Assert
        assertEquals(10L, result);
    }

    @Test
    void getNotificationCountByType_Success() {
        // Arrange
        when(notificationRepository.countByTypeAndActiveTrue(NotificationType.SMS)).thenReturn(15L);

        // Act
        long result = notificationService.getNotificationCountByType(NotificationType.SMS);

        // Assert
        assertEquals(15L, result);
    }

    @Test
    void getNotificationCountByLanguage_Success() {
        // Arrange
        when(notificationRepository.countByLanguageAndActiveTrue(NotificationLanguage.ENGLISH)).thenReturn(20L);

        // Act
        long result = notificationService.getNotificationCountByLanguage(NotificationLanguage.ENGLISH);

        // Assert
        assertEquals(20L, result);
    }

    @Test
    void getNotificationCountByRecipientAndStatus_Success() {
        // Arrange
        when(notificationRepository.countByRecipientIdAndStatusAndActiveTrue(1L, NotificationStatus.SENT)).thenReturn(3L);

        // Act
        long result = notificationService.getNotificationCountByRecipientAndStatus(1L, NotificationStatus.SENT);

        // Assert
        assertEquals(3L, result);
    }

    @Test
    void resolveTemplate_WithPlaceholders_Success() {
        // Arrange
        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(smsService.sendSms(anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
        // The actual resolution happens in the private method, but we can verify the service call succeeds
    }

    @Test
    void resolveTemplate_WithoutPlaceholders_Success() {
        // Arrange
        sendRequestDTO.setPlaceholders(null);

        when(notificationTemplateService.getTemplateEntityByCodeAndLanguage("DELIVERY_ASSIGNED", NotificationLanguage.ENGLISH))
                .thenReturn(testTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(smsService.sendSms(anyString(), anyString())).thenReturn(true);
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        when(notificationMapper.toResponseDTO(testNotification)).thenReturn(testNotificationResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.sendNotification(sendRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationResponseDTO.getId(), result.getId());
    }
}
