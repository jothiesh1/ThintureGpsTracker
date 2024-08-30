package com.GpsTracker.Thinture.service;
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;

@Service
public class VehicleHistoryService {

    @Autowired
    private VehicleHistoryRepository vehicleHistoryRepository;

    public void saveVehicleHistory(VehicleHistory history) {
        vehicleHistoryRepository.save(history);
    }
}
