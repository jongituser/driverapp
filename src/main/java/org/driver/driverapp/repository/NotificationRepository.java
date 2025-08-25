package org.driver.driverapp.repository;

import org.driver.driverapp.enums.NotificationLanguage;
import org.driver.driverapp.enums.NotificationStatus;
import org.driver.driverapp.enums.NotificationType;
import org.driver.driverapp.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find by recipient
    List<Notification> findByRecipientIdAndActiveTrue(Long recipientId);

    // Find by recipient with pagination
    Page<Notification> findByRecipientIdAndActiveTrue(Long recipientId, Pageable pageable);

    // Find by status
    List<Notification> findByStatusAndActiveTrue(NotificationStatus status);

    // Find by type
    List<Notification> findByTypeAndActiveTrue(NotificationType type);

    // Find by language
    List<Notification> findByLanguageAndActiveTrue(NotificationLanguage language);

    // Find by recipient and status
    List<Notification> findByRecipientIdAndStatusAndActiveTrue(Long recipientId, NotificationStatus status);

    // Find by recipient and type
    List<Notification> findByRecipientIdAndTypeAndActiveTrue(Long recipientId, NotificationType type);

    // Find by recipient and language
    List<Notification> findByRecipientIdAndLanguageAndActiveTrue(Long recipientId, NotificationLanguage language);

    // Find pending notifications
    List<Notification> findByStatusAndActiveTrueOrderByCreatedAtAsc(NotificationStatus status);

    // Find failed notifications
    List<Notification> findByStatusAndActiveTrueOrderByCreatedAtDesc(NotificationStatus status);

    // Find notifications created after a specific time
    @Query("SELECT n FROM Notification n WHERE n.active = true AND n.createdAt >= :since")
    List<Notification> findByCreatedAtAfter(@Param("since") Instant since);

    // Find notifications by recipient and date range
    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND n.active = true AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByRecipientIdAndCreatedAtBetween(@Param("recipientId") Long recipientId, 
                                                           @Param("startDate") Instant startDate, 
                                                           @Param("endDate") Instant endDate);

    // Count by recipient
    long countByRecipientIdAndActiveTrue(Long recipientId);

    // Count by status
    long countByStatusAndActiveTrue(NotificationStatus status);

    // Count by type
    long countByTypeAndActiveTrue(NotificationType type);

    // Count by language
    long countByLanguageAndActiveTrue(NotificationLanguage language);

    // Count by recipient and status
    long countByRecipientIdAndStatusAndActiveTrue(Long recipientId, NotificationStatus status);

    // Find recent notifications for a recipient
    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId AND n.active = true ORDER BY n.createdAt DESC")
    List<Notification> findRecentByRecipientId(@Param("recipientId") Long recipientId, Pageable pageable);
}
