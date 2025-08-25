package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.notification.request.CreateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.request.UpdateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationTemplateResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.exception.ResourceNotFoundException;
import org.driver.driverapp.mapper.NotificationTemplateMapper;
import org.driver.driverapp.model.NotificationTemplate;
import org.driver.driverapp.repository.NotificationTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationTemplateMapper notificationTemplateMapper;

    @Transactional
    public NotificationTemplateResponseDTO createTemplate(CreateNotificationTemplateRequestDTO requestDTO) {
        log.info("Creating notification template: {}", requestDTO.getCode());

        // Check if template already exists
        if (notificationTemplateRepository.existsByCodeAndLanguageAndActiveTrue(requestDTO.getCode(), requestDTO.getLanguage())) {
            throw new IllegalArgumentException("Template already exists for code: " + requestDTO.getCode() + " and language: " + requestDTO.getLanguage());
        }

        NotificationTemplate template = notificationTemplateMapper.toEntity(requestDTO);
        NotificationTemplate savedTemplate = notificationTemplateRepository.save(template);
        
        log.info("Notification template created successfully: {}", savedTemplate.getId());
        
        return notificationTemplateMapper.toResponseDTO(savedTemplate);
    }

    @Transactional
    public NotificationTemplateResponseDTO updateTemplate(Long id, UpdateNotificationTemplateRequestDTO requestDTO) {
        log.info("Updating notification template: {}", id);

        NotificationTemplate template = notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        template = notificationTemplateMapper.updateEntityFromDto(requestDTO, template);
        NotificationTemplate updatedTemplate = notificationTemplateRepository.save(template);
        
        log.info("Notification template updated successfully: {}", updatedTemplate.getId());
        
        return notificationTemplateMapper.toResponseDTO(updatedTemplate);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        log.info("Deleting notification template: {}", id);

        NotificationTemplate template = notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        template.setActive(false);
        notificationTemplateRepository.save(template);
        
        log.info("Notification template deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public NotificationTemplateResponseDTO getTemplateById(Long id) {
        log.info("Getting notification template by id: {}", id);

        NotificationTemplate template = notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found with id: " + id));

        return notificationTemplateMapper.toResponseDTO(template);
    }

    @Transactional(readOnly = true)
    public NotificationTemplateResponseDTO getTemplateByCodeAndLanguage(String code, NotificationLanguage language) {
        log.info("Getting notification template by code: {} and language: {}", code, language);

        NotificationTemplate template = notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue(code, language)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found for code: " + code + " and language: " + language));

        return notificationTemplateMapper.toResponseDTO(template);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> getTemplatesByCode(String code) {
        log.info("Getting notification templates by code: {}", code);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByCodeAndActiveTrue(code);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> getTemplatesByType(NotificationType type) {
        log.info("Getting notification templates by type: {}", type);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByTypeAndActiveTrue(type);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> getTemplatesByLanguage(NotificationLanguage language) {
        log.info("Getting notification templates by language: {}", language);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByLanguageAndActiveTrue(language);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> getTemplatesByTypeAndLanguage(NotificationType type, NotificationLanguage language) {
        log.info("Getting notification templates by type: {} and language: {}", type, language);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByTypeAndLanguageAndActiveTrue(type, language);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public Page<NotificationTemplateResponseDTO> getAllTemplates(Pageable pageable) {
        log.info("Getting all notification templates with pagination");

        Page<NotificationTemplate> templates = notificationTemplateRepository.findByActiveTrue(pageable);
        return templates.map(notificationTemplateMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> searchTemplatesByCode(String pattern) {
        log.info("Searching notification templates by code pattern: {}", pattern);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByCodeContaining(pattern);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponseDTO> searchTemplatesByBody(String content) {
        log.info("Searching notification templates by body content: {}", content);

        List<NotificationTemplate> templates = notificationTemplateRepository.findByBodyContaining(content);
        return notificationTemplateMapper.toResponseDTOList(templates);
    }

    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateEntityByCodeAndLanguage(String code, NotificationLanguage language) {
        log.info("Getting notification template entity by code: {} and language: {}", code, language);

        // Try to find template in requested language
        Optional<NotificationTemplate> template = notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue(code, language);
        
        if (template.isPresent()) {
            return template.get();
        }

        // Fallback to English if template not found in requested language
        if (language != NotificationLanguage.ENGLISH) {
            log.info("Template not found for language: {}, falling back to English", language);
            return notificationTemplateRepository.findByCodeAndLanguageAndActiveTrue(code, NotificationLanguage.ENGLISH)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification template not found for code: " + code + " in any language"));
        }

        throw new ResourceNotFoundException("Notification template not found for code: " + code + " and language: " + language);
    }

    @Transactional(readOnly = true)
    public long getTemplateCountByType(NotificationType type) {
        return notificationTemplateRepository.countByTypeAndActiveTrue(type);
    }

    @Transactional(readOnly = true)
    public long getTemplateCountByLanguage(NotificationLanguage language) {
        return notificationTemplateRepository.countByLanguageAndActiveTrue(language);
    }

    @Transactional(readOnly = true)
    public long getTemplateCountByCode(String code) {
        return notificationTemplateRepository.countByCodeAndActiveTrue(code);
    }
}
