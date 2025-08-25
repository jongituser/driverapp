package org.driver.driverapp.service.notification.impl;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.service.notification.SmsDeliveryReport;
import org.driver.driverapp.service.notification.SmsDeliveryStatus;
import org.driver.driverapp.service.notification.SmsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        log.info("SMS Service: Sending SMS to {} with message: {}", phoneNumber, message);
        
        // Simulate SMS sending with 95% success rate
        boolean success = Math.random() > 0.05;
        
        if (success) {
            log.info("SMS Service: SMS sent successfully to {}", phoneNumber);
        } else {
            log.warn("SMS Service: Failed to send SMS to {}", phoneNumber);
        }
        
        return success;
    }

    @Override
    public SmsDeliveryReport sendSmsWithReport(String phoneNumber, String message) {
        log.info("SMS Service: Sending SMS with report to {} with message: {}", phoneNumber, message);
        
        // Simulate SMS sending with delivery report
        boolean success = Math.random() > 0.05;
        String messageId = UUID.randomUUID().toString();
        
        SmsDeliveryReport report = SmsDeliveryReport.builder()
                .success(success)
                .messageId(messageId)
                .status(success ? "SENT" : "FAILED")
                .errorMessage(success ? null : "Network error")
                .timestamp(System.currentTimeMillis())
                .build();
        
        if (success) {
            log.info("SMS Service: SMS sent successfully to {} with message ID: {}", phoneNumber, messageId);
        } else {
            log.warn("SMS Service: Failed to send SMS to {} with message ID: {}", phoneNumber, messageId);
        }
        
        return report;
    }

    @Override
    public SmsDeliveryStatus checkDeliveryStatus(String messageId) {
        log.info("SMS Service: Checking delivery status for message ID: {}", messageId);
        
        // Simulate delivery status check
        double random = Math.random();
        SmsDeliveryStatus status;
        
        if (random < 0.7) {
            status = SmsDeliveryStatus.DELIVERED;
        } else if (random < 0.85) {
            status = SmsDeliveryStatus.PENDING;
        } else if (random < 0.95) {
            status = SmsDeliveryStatus.FAILED;
        } else {
            status = SmsDeliveryStatus.EXPIRED;
        }
        
        log.info("SMS Service: Delivery status for message ID {} is {}", messageId, status);
        return status;
    }

    @Override
    public int getRemainingBalance() {
        log.info("SMS Service: Getting remaining balance");
        
        // Simulate remaining balance (random between 100 and 1000)
        int balance = (int) (Math.random() * 900) + 100;
        
        log.info("SMS Service: Remaining balance: {}", balance);
        return balance;
    }
}
