package com.GpsTracker.Thinture.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Device;
import com.GpsTracker.Thinture.repository.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    public Device saveDevice(Device device) {
        logger.info("Saving device: {}", device);
        try {
            Device savedDevice = deviceRepository.save(device);
            logger.info("Device saved successfully with ID: {}", savedDevice.getId());
            return savedDevice;
        } catch (Exception e) {
            logger.error("Failed to save device: {}", device, e);
            throw e; // Re-throwing the exception for further handling.
        }
    }
}
