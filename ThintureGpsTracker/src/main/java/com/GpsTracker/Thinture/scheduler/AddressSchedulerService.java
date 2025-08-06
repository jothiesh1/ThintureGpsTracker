package com.GpsTracker.Thinture.scheduler;

import com.GpsTracker.Thinture.model.CoordinatesCache;
import com.GpsTracker.Thinture.repository.CoordinatesCacheRepository;
import com.GpsTracker.Thinture.service.GeocodingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(AddressSchedulerService.class);
    
    // ANSI Colors for logging
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    
    @Autowired
    private CoordinatesCacheRepository coordinatesCacheRepository;
    
    @Autowired
    private GeocodingService geocodingService;
    
    /**
     * 🕒 SCHEDULER: Automatically populate addresses every 5 minutes
     * Processes coordinates that don't have addresses yet
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes (300,000 ms)
    public void scheduleAddressPopulation() {
        try {
            logger.info("{}🕒 [SCHEDULER] Starting automatic address population{}",
                    ANSI_YELLOW, ANSI_RESET);
            
            // Get coordinates without addresses (limit to 10 per batch to avoid overwhelming API)
            Pageable pageable = PageRequest.of(0, 10);
            List<CoordinatesCache> coordinatesNeedingAddresses = 
                coordinatesCacheRepository.findCoordinatesWithoutAddresses(pageable);
            
            if (coordinatesNeedingAddresses.isEmpty()) {
                logger.info("{}🕒 [SCHEDULER] ✅ All coordinates have addresses - nothing to process{}",
                        ANSI_GREEN, ANSI_RESET);
                return;
            }
            
            logger.info("{}🕒 [SCHEDULER] Found {} coordinates needing addresses{}",
                    ANSI_BLUE, coordinatesNeedingAddresses.size(), ANSI_RESET);
            
            int successCount = 0;
            int errorCount = 0;
            
            // Process each coordinate
            for (CoordinatesCache coordinate : coordinatesNeedingAddresses) {
                try {
                    // Get address using GeocodingService (which handles caching automatically)
                    String address = geocodingService.getAddressFromCoordinates(
                        coordinate.getLatitude(), 
                        coordinate.getLongitude()
                    );
                    
                    successCount++;
                    logger.info("{}🕒 [SCHEDULER] ✅ Address populated: {},{} => {}{}",
                            ANSI_GREEN, coordinate.getLatitude(), coordinate.getLongitude(), 
                            address, ANSI_RESET);
                    
                    // Delay between API calls to respect OSM rate limits
                    Thread.sleep(2000); // 2 seconds between requests
                    
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("{}🕒 [SCHEDULER] ⚠️ Process interrupted{}",
                            ANSI_RED, ANSI_RESET);
                    break;
                    
                } catch (Exception e) {
                    errorCount++;
                    logger.error("{}🕒 [SCHEDULER] ❌ Failed to get address for {},{}: {}{}",
                            ANSI_RED, coordinate.getLatitude(), coordinate.getLongitude(), 
                            e.getMessage(), ANSI_RESET);
                }
            }
            
            // Log summary
            logger.info("{}🕒 [SCHEDULER] ✅ Batch completed - Success: {}, Errors: {}{}",
                    ANSI_CYAN, successCount, errorCount, ANSI_RESET);
            
            // Log remaining work
            long remainingCoordinates = coordinatesCacheRepository.countCoordinatesWithoutAddresses();
            if (remainingCoordinates > 0) {
                logger.info("{}🕒 [SCHEDULER] 📊 Still {} coordinates need addresses (will process in next run){}",
                        ANSI_YELLOW, remainingCoordinates, ANSI_RESET);
            } else {
                logger.info("{}🕒 [SCHEDULER] 🎉 All coordinates now have addresses!{}",
                        ANSI_GREEN, ANSI_RESET);
            }
            
        } catch (Exception e) {
            logger.error("{}🕒 [SCHEDULER] ❌ Scheduler failed: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET, e);
        }
    }
    
    /**
     * 🕒 ALTERNATIVE SCHEDULER: Run every 10 minutes (slower processing)
     * Uncomment this and comment the above if you want slower processing
     */
    /*
    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void scheduleAddressPopulationSlow() {
        // Same logic as above, but runs less frequently
    }
    */
    
    /**
     * 🕒 CRON SCHEDULER: Run at specific times (e.g., every hour at minute 0)
     * Uncomment this if you want to run at specific times
     */
    /*
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    public void scheduleAddressPopulationHourly() {
        scheduleAddressPopulation();
    }
    */
    
    /**
     * 📊 Log cache statistics periodically
     */
    @Scheduled(fixedRate = 900000) // Every 15 minutes
    public void logCacheStatistics() {
        try {
            long totalCoordinates = coordinatesCacheRepository.countCachedCoordinates();
            long withAddresses = coordinatesCacheRepository.countCoordinatesWithAddresses();
            long withoutAddresses = coordinatesCacheRepository.countCoordinatesWithoutAddresses();
            
            double completionPercentage = totalCoordinates > 0 ? 
                ((double) withAddresses / totalCoordinates) * 100 : 0.0;
            
            logger.info("{}📊 [CACHE STATS] Total: {} | With Addresses: {} | Without: {} | Completion: {:.1f}%{}",
                    ANSI_CYAN, totalCoordinates, withAddresses, withoutAddresses, 
                    completionPercentage, ANSI_RESET);
                    
        } catch (Exception e) {
            logger.error("{}📊 [CACHE STATS] Error getting statistics: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET);
        }
    }
}