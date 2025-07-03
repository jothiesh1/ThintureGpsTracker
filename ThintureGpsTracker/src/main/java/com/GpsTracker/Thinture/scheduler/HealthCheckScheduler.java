package com.GpsTracker.Thinture.scheduler;

//package com.thinture.gpstracker.scheduler;

// import com.thinture.gpstracker.alert.AlertManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.GpsTracker.Thinture.alert.AlertManager;

@Component
public class HealthCheckScheduler {

    @Autowired
    private AlertManager alertManager;

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkServerHealth() {
        boolean serverHealthy = performHealthCheck();

        if (!serverHealthy) {
            String errorDetails = "Health check failed: Server is unreachable.";
            alertManager.notifyServerDown(errorDetails);
        }
    }

    private boolean performHealthCheck() {
        // Example: Add logic to check server or database connectivity
        return true; // Simulate healthy server
    }
}
