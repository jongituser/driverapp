package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.notification.request.CreateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.request.UpdateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationTemplateResponseDTO;
import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.service.NotificationTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification-templates")
@RequiredArgsConstructor
public class NotificationTemplateController {

    private final NotificationTemplateService notificationTemplateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplateResponseDTO> createTemplate(@Valid @RequestBody CreateNotificationTemplateRequestDTO requestDTO) {
        log.info("Creating notification template: {}", requestDTO.getCode());
        NotificationTemplateResponseDTO response = notificationTemplateService.createTemplate(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTemplateResponseDTO> updateTemplate(@PathVariable Long id, 
                                                                        @Valid @RequestBody UpdateNotificationTemplateRequestDTO requestDTO) {
        log.info("Updating notification template: {}", id);
        NotificationTemplateResponseDTO response = notificationTemplateService.updateTemplate(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        log.info("Deleting notification template: {}", id);
        notificationTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<NotificationTemplateResponseDTO> getTemplateById(@PathVariable Long id) {
        log.info("Getting notification template by id: {}", id);
        NotificationTemplateResponseDTO response = notificationTemplateService.getTemplateById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}/language/{language}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<NotificationTemplateResponseDTO> getTemplateByCodeAndLanguage(@PathVariable String code, 
                                                                                       @PathVariable NotificationLanguage language) {
        log.info("Getting notification template by code: {} and language: {}", code, language);
        NotificationTemplateResponseDTO response = notificationTemplateService.getTemplateByCodeAndLanguage(code, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> getTemplatesByCode(@PathVariable String code) {
        log.info("Getting notification templates by code: {}", code);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.getTemplatesByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> getTemplatesByType(@PathVariable NotificationType type) {
        log.info("Getting notification templates by type: {}", type);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.getTemplatesByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/language/{language}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> getTemplatesByLanguage(@PathVariable NotificationLanguage language) {
        log.info("Getting notification templates by language: {}", language);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.getTemplatesByLanguage(language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}/language/{language}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> getTemplatesByTypeAndLanguage(@PathVariable NotificationType type, 
                                                                                              @PathVariable NotificationLanguage language) {
        log.info("Getting notification templates by type: {} and language: {}", type, language);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.getTemplatesByTypeAndLanguage(type, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<NotificationTemplateResponseDTO>> getAllTemplates(Pageable pageable) {
        log.info("Getting all notification templates with pagination");
        Page<NotificationTemplateResponseDTO> response = notificationTemplateService.getAllTemplates(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/code")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> searchTemplatesByCode(@RequestParam String pattern) {
        log.info("Searching notification templates by code pattern: {}", pattern);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.searchTemplatesByCode(pattern);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/body")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<NotificationTemplateResponseDTO>> searchTemplatesByBody(@RequestParam String content) {
        log.info("Searching notification templates by body content: {}", content);
        List<NotificationTemplateResponseDTO> response = notificationTemplateService.searchTemplatesByBody(content);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Long> getTemplateCountByType(@PathVariable NotificationType type) {
        log.info("Getting template count by type: {}", type);
        long count = notificationTemplateService.getTemplateCountByType(type);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/language/{language}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Long> getTemplateCountByLanguage(@PathVariable NotificationLanguage language) {
        log.info("Getting template count by language: {}", language);
        long count = notificationTemplateService.getTemplateCountByLanguage(language);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Long> getTemplateCountByCode(@PathVariable String code) {
        log.info("Getting template count by code: {}", code);
        long count = notificationTemplateService.getTemplateCountByCode(code);
        return ResponseEntity.ok(count);
    }
}
