package org.driver.driverapp.service.notification;

public interface EmailService {
    
    /**
     * Send email notification
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @return true if sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String body);
    
    /**
     * Send email notification with delivery report
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @return delivery report with status and message ID
     */
    EmailDeliveryReport sendEmailWithReport(String to, String subject, String body);
    
    /**
     * Send HTML email
     * @param to recipient email address
     * @param subject email subject
     * @param htmlBody HTML email body
     * @return true if sent successfully, false otherwise
     */
    boolean sendHtmlEmail(String to, String subject, String htmlBody);
    
    /**
     * Check email delivery status
     * @param messageId message ID from previous send
     * @return delivery status
     */
    EmailDeliveryStatus checkDeliveryStatus(String messageId);
}
