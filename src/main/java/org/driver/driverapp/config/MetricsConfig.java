package org.driver.driverapp.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    // Metric names
    public static final String DELIVERY_COUNTER = "delivery_total";
    public static final String DELIVERY_SUCCESS_COUNTER = "delivery_success_total";
    public static final String DELIVERY_FAILED_COUNTER = "delivery_failed_total";
    public static final String PAYMENT_PROCESSED_COUNTER = "payment_processed_total";
    public static final String PAYMENT_FAILED_COUNTER = "payment_failed_total";
    public static final String DRIVER_ACTIVE_GAUGE = "driver_active_total";
    public static final String DRIVER_UPTIME_TIMER = "driver_uptime_seconds";
    public static final String DELIVERY_DURATION_TIMER = "delivery_duration_seconds";
    public static final String PAYMENT_PROCESSING_TIMER = "payment_processing_seconds";
    public static final String NOTIFICATION_SENT_COUNTER = "notification_sent_total";
    public static final String AUDIT_LOG_CREATED_COUNTER = "audit_log_created_total";
    public static final String CACHE_HIT_COUNTER = "cache_hit_total";
    public static final String CACHE_MISS_COUNTER = "cache_miss_total";
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public Counter deliveryCounter(MeterRegistry registry) {
        return Counter.builder(DELIVERY_COUNTER)
                .description("Total number of deliveries")
                .register(registry);
    }
    
    @Bean
    public Counter deliverySuccessCounter(MeterRegistry registry) {
        return Counter.builder(DELIVERY_SUCCESS_COUNTER)
                .description("Total number of successful deliveries")
                .register(registry);
    }
    
    @Bean
    public Counter deliveryFailedCounter(MeterRegistry registry) {
        return Counter.builder(DELIVERY_FAILED_COUNTER)
                .description("Total number of failed deliveries")
                .register(registry);
    }
    
    @Bean
    public Counter paymentProcessedCounter(MeterRegistry registry) {
        return Counter.builder(PAYMENT_PROCESSED_COUNTER)
                .description("Total number of processed payments")
                .register(registry);
    }
    
    @Bean
    public Counter paymentFailedCounter(MeterRegistry registry) {
        return Counter.builder(PAYMENT_FAILED_COUNTER)
                .description("Total number of failed payments")
                .register(registry);
    }
    
    @Bean
    public Gauge driverActiveGauge(MeterRegistry registry) {
        return Gauge.builder(DRIVER_ACTIVE_GAUGE, () -> 0) // Will be updated dynamically
                .description("Number of active drivers")
                .register(registry);
    }
    
    @Bean
    public Timer driverUptimeTimer(MeterRegistry registry) {
        return Timer.builder(DRIVER_UPTIME_TIMER)
                .description("Driver uptime duration")
                .register(registry);
    }
    
    @Bean
    public Timer deliveryDurationTimer(MeterRegistry registry) {
        return Timer.builder(DELIVERY_DURATION_TIMER)
                .description("Delivery duration")
                .register(registry);
    }
    
    @Bean
    public Timer paymentProcessingTimer(MeterRegistry registry) {
        return Timer.builder(PAYMENT_PROCESSING_TIMER)
                .description("Payment processing duration")
                .register(registry);
    }
    
    @Bean
    public Counter notificationSentCounter(MeterRegistry registry) {
        return Counter.builder(NOTIFICATION_SENT_COUNTER)
                .description("Total number of notifications sent")
                .register(registry);
    }
    
    @Bean
    public Counter auditLogCreatedCounter(MeterRegistry registry) {
        return Counter.builder(AUDIT_LOG_CREATED_COUNTER)
                .description("Total number of audit logs created")
                .register(registry);
    }
    
    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder(CACHE_HIT_COUNTER)
                .description("Total number of cache hits")
                .register(registry);
    }
    
    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder(CACHE_MISS_COUNTER)
                .description("Total number of cache misses")
                .register(registry);
    }
}

