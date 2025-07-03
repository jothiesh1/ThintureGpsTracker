package com.GpsTracker.Thinture.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class LocationCacheService {

    private final Map<String, String> localCache = new HashMap<>(); // Temporary memory cache

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getAddressFromDatabase(String key) {
        // ✅ Check in-memory cache first
        if (localCache.containsKey(key)) {
            return localCache.get(key);
        }

        try {
            String sql = "SELECT address FROM location_cache WHERE key = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{key}, String.class);
        } catch (Exception e) {
            return null; // Not found in DB
        }
    }

    public void saveAddressToDatabase(String key, String address) {
        localCache.put(key, address); // Store in memory
        try {
            String sql = "INSERT INTO location_cache (key, address) VALUES (?, ?) ON DUPLICATE KEY UPDATE address=?";
            jdbcTemplate.update(sql, key, address, address);
        } catch (Exception e) {
            System.err.println("Error saving address to DB: " + e.getMessage());
        }
    }
}
