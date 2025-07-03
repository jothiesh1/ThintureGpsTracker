package com.GpsTracker.Thinture.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import java.text.SimpleDateFormat;

@Service
public class VehicleLastLocationService {

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;
    private static final Logger logger = LoggerFactory.getLogger(VehicleLastLocationService.class);

  
    /**
     * Count all vehicles.
     *
     * @return Total vehicle count.
     */
    public long countAllVehicles() {
        logger.info("Counting all vehicles.");
        long count = vehicleLastLocationRepository.count();
        logger.info("Total vehicles: {}", count);
        return count;
    }

    /**
     * Count vehicles by their status.
     *
     * @param status The status to filter by.
     * @return Count of vehicles with the specified status.
     */
    public long countVehiclesByStatus(String status) {
        logger.info("Counting vehicles with status: {}", status);
        long count = vehicleLastLocationRepository.countByStatus(status);
        logger.info("Total vehicles with status {}: {}", status, count);
        return count;
    }

    /**
     * Fetch the last known location for a specific device ID.
     *
     * @param deviceID The device ID to search for.
     * @return The latest VehicleLastLocation for the specified device ID.
     */
  
   

    public List<VehicleLastLocation> getAllLastKnownLocations() {
        logger.info("Fetching all last known locations from the database...");
        List<VehicleLastLocation> locations = vehicleLastLocationRepository.findAll();
        logger.info("Fetched {} locations from the database.", locations.size());

        if (!locations.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            locations.forEach(location -> {
                String formattedDate = dateFormat.format(location.getTimestamp());
                logger.debug("Location Data - ID: {}, DeviceID: {}, Timestamp: {}, Latitude: {}, Longitude: {}",
                    location.getId(), location.getDeviceId(), formattedDate,
                    location.getLatitude(), location.getLongitude());
            });
        } else {
            logger.warn("No locations found in the database.");
        }

        return locations;
    }

    public VehicleLastLocation getLastKnownLocation(String deviceID) {
        if (deviceID == null || deviceID.isEmpty()) {
            logger.warn("Invalid deviceID provided.");
            throw new IllegalArgumentException("Device ID cannot be null or empty.");
        }

        logger.info("Fetching last known location for deviceID: {}", deviceID);
        VehicleLastLocation location = vehicleLastLocationRepository.findByDeviceId(deviceID).orElse(null);

        if (location != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(location.getTimestamp());
            logger.info("Found Location - ID: {}, DeviceID: {}, Timestamp: {}, Latitude: {}, Longitude: {}",
                location.getId(), location.getDeviceId(), formattedDate,
                location.getLatitude(), location.getLongitude());
        } else {
            logger.warn("No location found for deviceID: {}", deviceID);
        }

        return location;
    }




}


