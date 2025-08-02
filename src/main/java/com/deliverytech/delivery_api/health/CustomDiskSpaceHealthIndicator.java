package com.deliverytech.delivery_api.health;

import java.io.File;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customDiskSpace")
public class CustomDiskSpaceHealthIndicator implements HealthIndicator {

    private static final long THRESHOLD = 1024 * 1024 * 100L;

    @Override
    public Health health() {
        try {
            File diskPath = new File(".");
            long freeSpace = diskPath.getTotalSpace();

            if (freeSpace > THRESHOLD) {
                return Health.up().withDetail("free-space-mb", freeSpace / (1024 * 1024))
                        .withDetail("free-space-mb", THRESHOLD / (1024 * 1024)).build();
            }

            return Health.down().withDetail("free-space-mb", freeSpace / (1024 * 1024))
                    .withDetail("free-space-mb", THRESHOLD / (1024 * 1024))
                    .withDetail("Warning", "Espaço em disco baixo.").build();

        } catch (Exception e) {
            return Health.down(e).withDetail("error", e.getMessage()).build();
        }
    }
}
