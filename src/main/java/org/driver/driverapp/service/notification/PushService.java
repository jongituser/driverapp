package org.driver.driverapp.service.notification;

import java.util.List;
import java.util.Map;

public interface PushService {
    
    /**
     * Send push notification to a single device
     * @param deviceToken device token
     * @param title notification title
     * @param body notification body
     * @return true if sent successfully, false otherwise
     */
    boolean sendPushNotification(String deviceToken, String title, String body);
    
    /**
     * Send push notification with custom data
     * @param deviceToken device token
     * @param title notification title
     * @param body notification body
     * @param data custom data payload
     * @return true if sent successfully, false otherwise
     */
    boolean sendPushNotificationWithData(String deviceToken, String title, String body, Map<String, String> data);
    
    /**
     * Send push notification to multiple devices
     * @param deviceTokens list of device tokens
     * @param title notification title
     * @param body notification body
     * @return delivery report with success/failure counts
     */
    PushDeliveryReport sendPushNotificationToMultiple(List<String> deviceTokens, String title, String body);
    
    /**
     * Send push notification to topic
     * @param topic topic name
     * @param title notification title
     * @param body notification body
     * @return true if sent successfully, false otherwise
     */
    boolean sendPushNotificationToTopic(String topic, String title, String body);
    
    /**
     * Subscribe device to topic
     * @param deviceToken device token
     * @param topic topic name
     * @return true if subscribed successfully, false otherwise
     */
    boolean subscribeToTopic(String deviceToken, String topic);
    
    /**
     * Unsubscribe device from topic
     * @param deviceToken device token
     * @param topic topic name
     * @return true if unsubscribed successfully, false otherwise
     */
    boolean unsubscribeFromTopic(String deviceToken, String topic);
}
