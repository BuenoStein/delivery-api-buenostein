package com.deliverytech.delivery_api.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("externalService")
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private boolean serviceUp = true;

    @Override
    public Health health() {
        try {
            if (serviceUp) {
                return Health.up().withDetail("ServiceName", "Payment gateway")
                        .withDetail("status", "Reachable").build();
            }

            return Health.down().withDetail("ServiceName", "Payment gateway")
                    .withDetail("status", "Service not reachable or responding").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("error", e.getMessage()).build();
        }
    }

    public void setServiceUp(boolean serviceUp) {
        this.serviceUp = serviceUp;
    }
}
