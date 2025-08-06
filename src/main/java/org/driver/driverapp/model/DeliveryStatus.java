package org.driver.driverapp.model;

public enum DeliveryStatus {
    PENDING,   // created, waiting pickup
    PICKED_UP, // collected from partner
    IN_TRANSIT,
    DELIVERED,
    CANCELED,
    OVERDUE,
    IN_PROGRESS
}
