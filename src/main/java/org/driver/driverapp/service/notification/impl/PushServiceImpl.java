package org.driver.driverapp.service.notification.impl;

import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.service.notification.PushDeliveryReport;
import org.driver.driverapp.service.notification.PushService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    @Override
    public boolean sendPushNotification(String deviceToken, String title, String body) {
        log.info("Push Service: Sending push notification to device {} with title: {}", deviceToken, title);
        
        // Simulate push notification sending with 90% success rate
        boolean success = Math.random() > 0.1;
        
        if (success) {
            log.info("Push Service: Push notification sent successfully to device {}", deviceToken);
        } else {
            log.warn("Push Service: Failed to send push notification to device {}", deviceToken);
        }
        
        return success;
    }

    @Override
    public boolean sendPushNotificationWithData(String deviceToken, String title, String body, Map<String, String> data) {
        log.info("Push Service: Sending push notification with data to device {} with title: {}", deviceToken, title);
        
        // Simulate push notification with data sending with 90% success rate
        boolean success = Math.random() > 0.1;
        
        if (success) {
            log.info("Push Service: Push notification with data sent successfully to device {}", deviceToken);
        } else {
            log.warn("Push Service: Failed to send push notification with data to device {}", deviceToken);
        }
        
        return success;
    }

    @Override
    public PushDeliveryReport sendPushNotificationToMultiple(List<String> deviceTokens, String title, String body) {
        log.info("Push Service: Sending push notification to {} devices with title: {}", deviceTokens.size(), title);
        
        int totalDevices = deviceTokens.size();
        int successfulDeliveries = 0;
        List<String> failedDeviceTokens = new ArrayList<>();
        
        // Simulate sending to multiple devices
        for (String deviceToken : deviceTokens) {
            boolean success = Math.random() > 0.1;
            if (success) {
                successfulDeliveries++;
            } else {
                failedDeviceTokens.add(deviceToken);
            }
        }
        
        int failedDeliveries = totalDevices - successfulDeliveries;
        boolean overallSuccess = successfulDeliveries > 0;
        
        PushDeliveryReport report = PushDeliveryReport.builder()
                .success(overallSuccess)
                .totalDevices(totalDevices)
                .successfulDeliveries(successfulDeliveries)
                .failedDeliveries(failedDeliveries)
                .failedDeviceTokens(failedDeviceTokens)
                .errorMessage(overallSuccess ? null : "All deliveries failed")
                .timestamp(System.currentTimeMillis())
                .build();
        
        log.info("Push Service: Push notification sent to {} devices. Success: {}, Failed: {}", 
                totalDevices, successfulDeliveries, failedDeliveries);
        
        return report;
    }

    @Override
    public boolean sendPushNotificationToTopic(String topic, String title, String body) {
        log.info("Push Service: Sending push notification to topic {} with title: {}", topic, title);
        
        // Simulate topic notification sending with 95% success rate
        boolean success = Math.random() > 0.05;
        
        if (success) {
            log.info("Push Service: Push notification sent successfully to topic {}", topic);
        } else {
            log.warn("Push Service: Failed to send push notification to topic {}", topic);
        }
        
        return success;
    }

    @Override
    public boolean subscribeToTopic(String deviceToken, String topic) {
        log.info("Push Service: Subscribing device {} to topic {}", deviceToken, topic);
        
        // Simulate topic subscription with 98% success rate
        boolean success = Math.random() > 0.02;
        
        if (success) {
            log.info("Push Service: Device {} subscribed successfully to topic {}", deviceToken, topic);
        } else {
            log.warn("Push Service: Failed to subscribe device {} to topic {}", deviceToken, topic);
        }
        
        return success;
    }

    @Override
    public boolean unsubscribeFromTopic(String deviceToken, String topic) {
        log.info("Push Service: Unsubscribing device {} from topic {}", deviceToken, topic);
        
        // Simulate topic unsubscription with 98% success rate
        boolean success = Math.random() > 0.02;
        
        if (success) {
            log.info("Push Service: Device {} unsubscribed successfully from topic {}", deviceToken, topic);
        } else {
            log.warn("Push Service: Failed to unsubscribe device {} from topic {}", deviceToken, topic);
        }
        
        return success;
    }
}
