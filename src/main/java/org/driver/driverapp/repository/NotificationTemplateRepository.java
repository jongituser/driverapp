package org.driver.driverapp.repository;

import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.model.NotificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    // Find by code and language
    Optional<NotificationTemplate> findByCodeAndLanguageAndActiveTrue(String code, NotificationLanguage language);

    // Find by code (all languages)
    List<NotificationTemplate> findByCodeAndActiveTrue(String code);

    // Find by type and language
    List<NotificationTemplate> findByTypeAndLanguageAndActiveTrue(NotificationType type, NotificationLanguage language);

    // Find by type
    List<NotificationTemplate> findByTypeAndActiveTrue(NotificationType type);

    // Find by language
    List<NotificationTemplate> findByLanguageAndActiveTrue(NotificationLanguage language);

    // Find by code and type
    List<NotificationTemplate> findByCodeAndTypeAndActiveTrue(String code, NotificationType type);

    // Search by code pattern
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.active = true AND nt.code LIKE %:pattern%")
    List<NotificationTemplate> findByCodeContaining(@Param("pattern") String pattern);

    // Search by body content
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.active = true AND nt.body LIKE %:content%")
    List<NotificationTemplate> findByBodyContaining(@Param("content") String content);

    // Find all active templates with pagination
    Page<NotificationTemplate> findByActiveTrue(Pageable pageable);

    // Find by type and language with pagination
    Page<NotificationTemplate> findByTypeAndLanguageAndActiveTrue(NotificationType type, NotificationLanguage language, Pageable pageable);

    // Count by type
    long countByTypeAndActiveTrue(NotificationType type);

    // Count by language
    long countByLanguageAndActiveTrue(NotificationLanguage language);

    // Count by code
    long countByCodeAndActiveTrue(String code);

    // Check if template exists
    boolean existsByCodeAndLanguageAndActiveTrue(String code, NotificationLanguage language);
}
