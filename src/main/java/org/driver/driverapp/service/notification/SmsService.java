package org.driver.driverapp.service.notification;

public interface SmsService {
    
    /**
     * Send SMS notification
     * @param phoneNumber recipient phone number
     * @param message message content
     * @return true if sent successfully, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);
    
    /**
     * Send SMS notification with delivery report
     * @param phoneNumber recipient phone number
     * @param message message content
     * @return delivery report with status and message ID
     */
    SmsDeliveryReport sendSmsWithReport(String phoneNumber, String message);
    
    /**
     * Check SMS delivery status
     * @param messageId message ID from previous send
     * @return delivery status
     */
    SmsDeliveryStatus checkDeliveryStatus(String messageId);
    
    /**
     * Get remaining SMS balance
     * @return remaining balance
     */
    int getRemainingBalance();
}
