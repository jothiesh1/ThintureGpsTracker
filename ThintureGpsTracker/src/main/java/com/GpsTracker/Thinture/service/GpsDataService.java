package com.GpsTracker.Thinture.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.repository.GpsDataRepository;

@Service
public class GpsDataService {
    @Autowired
    private GpsDataRepository gpsDataRepository;

    public GpsData saveOrUpdateGpsData(GpsData gpsData) {
        Optional<GpsData> existingGpsData = gpsDataRepository.findByDeviceID(gpsData.getDeviceID());
        if (existingGpsData.isPresent()) {
            GpsData existingData = existingGpsData.get();
            existingData.setTimestamp(gpsData.getTimestamp());
            existingData.setLatitude(gpsData.getLatitude());
            existingData.setLongitude(gpsData.getLongitude());
            return gpsDataRepository.save(existingData);
        } else {
            return gpsDataRepository.save(gpsData);
        }
    }
    // Method to save GPS data
    
    public void saveGpsData(GpsData gpsData) {
        gpsDataRepository.save(gpsData);
    }

    public Optional<GpsData> findByDeviceID(String deviceID) {
        return gpsDataRepository.findByDeviceID(deviceID);
    }

    public void deleteByDeviceID(String deviceID) {
        gpsDataRepository.deleteByDeviceID(deviceID);
    }
 // Method to retrieve all last known locations for each device
    public List<GpsData> findAllLastGpsData() {
        return gpsDataRepository.findAll();
    }
}