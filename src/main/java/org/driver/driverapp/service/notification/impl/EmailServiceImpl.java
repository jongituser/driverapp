package org.driver.driverapp.service.notification.impl;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.service.notification.EmailDeliveryReport;
import org.driver.driverapp.service.notification.EmailDeliveryStatus;
import org.driver.driverapp.service.notification.EmailService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        log.info("Email Service: Sending email to {} with subject: {}", to, subject);
        
        // Simulate email sending with 98% success rate
        boolean success = Math.random() > 0.02;
        
        if (success) {
            log.info("Email Service: Email sent successfully to {}", to);
        } else {
            log.warn("Email Service: Failed to send email to {}", to);
        }
        
        return success;
    }

    @Override
    public EmailDeliveryReport sendEmailWithReport(String to, String subject, String body) {
        log.info("Email Service: Sending email with report to {} with subject: {}", to, subject);
        
        // Simulate email sending with delivery report
        boolean success = Math.random() > 0.02;
        String messageId = UUID.randomUUID().toString();
        
        EmailDeliveryReport report = EmailDeliveryReport.builder()
                .success(success)
                .messageId(messageId)
                .status(success ? "SENT" : "FAILED")
                .errorMessage(success ? null : "SMTP error")
                .timestamp(System.currentTimeMillis())
                .build();
        
        if (success) {
            log.info("Email Service: Email sent successfully to {} with message ID: {}", to, messageId);
        } else {
            log.warn("Email Service: Failed to send email to {} with message ID: {}", to, messageId);
        }
        
        return report;
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        log.info("Email Service: Sending HTML email to {} with subject: {}", to, subject);
        
        // Simulate HTML email sending with 98% success rate
        boolean success = Math.random() > 0.02;
        
        if (success) {
            log.info("Email Service: HTML email sent successfully to {}", to);
        } else {
            log.warn("Email Service: Failed to send HTML email to {}", to);
        }
        
        return success;
    }

    @Override
    public EmailDeliveryStatus checkDeliveryStatus(String messageId) {
        log.info("Email Service: Checking delivery status for message ID: {}", messageId);
        
        // Simulate delivery status check
        double random = Math.random();
        EmailDeliveryStatus status;
        
        if (random < 0.8) {
            status = EmailDeliveryStatus.DELIVERED;
        } else if (random < 0.9) {
            status = EmailDeliveryStatus.SENT;
        } else if (random < 0.95) {
            status = EmailDeliveryStatus.FAILED;
        } else if (random < 0.98) {
            status = EmailDeliveryStatus.BOUNCED;
        } else {
            status = EmailDeliveryStatus.PENDING;
        }
        
        log.info("Email Service: Delivery status for message ID {} is {}", messageId, status);
        return status;
    }
}
